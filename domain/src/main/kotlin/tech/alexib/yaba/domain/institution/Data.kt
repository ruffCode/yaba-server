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

sealed class InstitutionError {
    object InvalidInstitutionId : InstitutionError()
}

fun interface GetOrCreateInstitution {
    suspend operator fun invoke(id: InstitutionId): Either<InstitutionError, Institution>
}


suspend inline fun InstitutionId.getOrCreate(
    crossinline getInstitutionFromDb: GetInstitutionFromDb,
    crossinline getInstitutionFromPlaid: GetInstitutionFromPlaid,
    crossinline saveInstitution: SaveInstitution,
): Either<InstitutionError, Institution> {
    val id = this
    val existingInstitution = getInstitutionFromDb(id)
    return if (existingInstitution != null) {
        existingInstitution.right()
    } else {
        println("saving from plaid")
        getInstitutionFromPlaid(id).map { saveInstitution(it) }
    }
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
    US,
    GB,
    ES,
    NL,
    FR,
    IE,
    CA
}
