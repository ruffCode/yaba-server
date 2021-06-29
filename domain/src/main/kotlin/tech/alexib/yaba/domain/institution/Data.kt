package tech.alexib.yaba.domain.institution

import arrow.core.Either
import arrow.core.right
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class InstitutionId(val value: String)

@Serializable
data class Institution(
    val institutionId: InstitutionId,
    val name: String,
    val products: List<Product>,
    val countryCodes: List<CountryCode>,
    val url: String? = null,
    val primaryColor: String? = null,
    val logo: String,
    val routingNumbers: List<String>? = null,
    val oauth: Boolean,
)

typealias GetInstitutionFromPlaid = suspend (InstitutionId) -> Either<InstitutionError.InvalidInstitutionId, Institution>
typealias SaveInstitution = suspend (Institution) -> Institution
typealias GetInstitutionFromDb = suspend (InstitutionId) -> Institution?
//typealias GetOrCreateInstitution =
//        suspend (InstitutionId, GetInstitutionFromDb, GetInstitutionFromPlaid, SaveInstitution) ->
//        Either<InstitutionError, Institution>

sealed class InstitutionError {
    object InvalidInstitutionId : InstitutionError()
}

fun interface GetOrCreateInstitution {
    suspend operator fun invoke(id: InstitutionId): Either<InstitutionError, Institution>
}


suspend inline fun InstitutionId.getOrCreate(
    crossinline getInstitutionFromDb: GetInstitutionFromDb,
    crossinline getInstitutionFromPlaid: GetInstitutionFromPlaid,
    crossinline saveInstitution: SaveInstitution
): Either<InstitutionError, Institution> {
    val id = this
    val existingInstitution = getInstitutionFromDb(id)
    println("existingInstitution is null ${ existingInstitution == null}")
    return if (existingInstitution != null) {
        existingInstitution.right()
    } else {
        println("saving from plaid")
        getInstitutionFromPlaid(id).map { saveInstitution(it) }
    }

//    return either {
//        val existingInstitution = getInstitutionFromDb(id)
//
//
//        (existingInstitution?.right() ?: getInstitutionFromPlaid(id).map { saveInstitution(it) }).bind()
//
//    }
}

@Serializable
enum class Product {
    @SerialName("assets")
    ASSETS,

    @SerialName("auth")
    AUTH,

    @SerialName("balance")
    BALANCE,

    @SerialName("identity")
    IDENTITY,

    @SerialName("investments")
    INVESTMENTS,

    @SerialName("liabilities")
    LIABILITIES,

    @SerialName("payment_initiation")
    PAYMENT_INITIATION,

    @SerialName("transactions")
    TRANSACTIONS,

    @SerialName("credit_details")
    CREDIT_DETAILS,

    @SerialName("income")
    INCOME,

    @SerialName("deposit_switch")
    DEPOSIT_SWITCH
}

@Serializable
enum class CountryCode {
    @SerialName("US")
    US,

    @SerialName("GB")
    GB,

    @SerialName("ES")
    ES,

    @SerialName("NL")
    NL,

    @SerialName("FR")
    FR,

    @SerialName("IE")
    IE,

    @SerialName("CA")
    CA
}
