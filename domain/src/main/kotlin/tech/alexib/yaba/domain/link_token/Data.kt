package tech.alexib.yaba.domain.link_token

import tech.alexib.yaba.domain.common.AccessToken
import tech.alexib.yaba.domain.item.ItemId

@JvmInline
value class LinkToken(val value: String)

typealias GetAccessTokenByItemId = suspend (ItemId) -> AccessToken
