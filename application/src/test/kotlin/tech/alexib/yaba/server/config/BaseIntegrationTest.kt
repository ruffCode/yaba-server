package tech.alexib.yaba.server.config

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import tech.alexib.yaba.server.YabaServerApplication

@SpringBootTest(classes = [YabaServerApplication::class])
@ActiveProfiles("test")
abstract class BaseIntegrationTest
