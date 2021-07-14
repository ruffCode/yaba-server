package tech.alexib.yaba.server.config

import io.netty.buffer.ByteBufAllocator
import io.r2dbc.postgresql.api.PostgresqlConnection
import io.r2dbc.postgresql.codec.CodecRegistry
import io.r2dbc.postgresql.codec.EnumCodec
import io.r2dbc.postgresql.extension.CodecRegistrar
import org.reactivestreams.Publisher
import tech.alexib.yaba.domain.user.UserRole

class EnumCodecRegistrar : CodecRegistrar {
    override fun register(
        connection: PostgresqlConnection,
        allocator: ByteBufAllocator,
        registry: CodecRegistry
    ): Publisher<Void> {
        return EnumCodec.builder()
            .withEnum("user_role", UserRole::class.java)
            .build()
            .register(connection, allocator, registry)
    }
}