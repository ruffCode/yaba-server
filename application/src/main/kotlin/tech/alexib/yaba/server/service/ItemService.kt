package tech.alexib.yaba.server.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import mu.KotlinLogging
import org.springframework.stereotype.Service
import tech.alexib.plaid.client.model.PlaidError
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.item.Item
import tech.alexib.yaba.domain.item.ItemCreateError
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.item.LinkItemRequest
import tech.alexib.yaba.domain.item.PlaidAccessToken
import tech.alexib.yaba.domain.item.PlaidApiError
import tech.alexib.yaba.domain.item.PlaidItemId
import tech.alexib.yaba.domain.item.PublicToken
import tech.alexib.yaba.domain.item.PublicTokenExchangeResponse
import tech.alexib.yaba.domain.item.create
import tech.alexib.yaba.domain.item.validate
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.feature.account.AccountRepository
import tech.alexib.yaba.server.feature.item.ItemRepository
import tech.alexib.yaba.server.feature.item.toEntity
import tech.alexib.yaba.server.feature.transaction.TransactionRepository
import tech.alexib.yaba.server.plaid.PlaidService

interface ItemService {
    suspend fun linkItem(linkItemRequest: LinkItemRequest): Either<ItemCreateError, Item>
    suspend fun unlinkItem(itemId: ItemId, userId: UserId)
}

private val logger = KotlinLogging.logger {}

@Service
class ItemServiceImpl(
    private val plaidService: PlaidService,
    private val institutionService: InstitutionService,
    private val itemRepository: ItemRepository,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : ItemService {

    override suspend fun linkItem(linkItemRequest: LinkItemRequest): Either<ItemCreateError, Item> {
        val createResult = linkItemRequest.create(
            exchangePublicToken = ::exchangePublicToken,
            createItem = ::createItem,
            ::relink
        ) {
            it.validate(
                getItemByInstitutionIdWithTimesUnlinked = ::getItemByInstitutionIdWithTimesUnlinked,
            )
        }
        institutionService.getOrCreate(linkItemRequest.institutionId).mapLeft {
            logger.error { it }
        }
        return createResult
    }

    override suspend fun unlinkItem(itemId: ItemId, userId: UserId) {

        itemRepository.unlink(itemId, userId)

        itemRepository.findById(itemId).map { itemEntity ->
            transactionRepository.deleteTransactions(itemId)
            accountRepository.deleteByItemId(itemId)
            plaidService.removeItem(itemEntity.accessToken)
        }

    }

    private suspend fun relink(institutionId: InstitutionId, userId: UserId, plaidAccessToken: PlaidAccessToken): Item =
        itemRepository.relink(institutionId, userId, plaidAccessToken).toDomain()


    private suspend fun exchangePublicToken(publicToken: PublicToken): Either<PlaidApiError, PublicTokenExchangeResponse> {
        return runCatching {
            plaidService.exchangeToken(publicToken.value).let {
                PublicTokenExchangeResponse(PlaidAccessToken(it.accessToken), PlaidItemId(it.itemId))
            }.right()
        }.getOrElse { e ->
            when (e) {
                is PlaidError -> PlaidApiError(e.errorMessage).left()
                else -> throw  e
            }
        }
    }

    private suspend fun getItemByInstitutionIdWithTimesUnlinked(
        institutionId: InstitutionId,
        userId: UserId
    ): Either<Pair<Item, Int>, Unit> =
        itemRepository.findByInstitutionId(
            institutionId.value, userId
        ).fold({ Unit.right() }, {
            Pair(it.toDomain(), it.timesUnlinked).left()
        })

    private suspend fun createItem(item: Item): Item {
        itemRepository.createItem(item.toEntity())
        return item
    }
}
