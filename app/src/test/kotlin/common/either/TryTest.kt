package common.either

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.net.URL

class TryTest {
  fun parseLong(s: String): Either<NumberFormatException, Long> =
      tryEither { s.toLong() }

  @Test fun testExceptions() {
    assertThat(parseLong("fuck").isLeft()).isTrue()
    assertThat(parseLong("123")).isEqualTo(Right(123L))

    assertThat(tryEither<IllegalStateException, Any> { tryIo { error("fuck") } }.mapLeft { it.message })
        .isEqualTo(tryEither<IllegalStateException, Any> { error("fuck") }.mapLeft { it.message })
    assertThat(tryIo { URL("fuck") }.isLeft()).isTrue()
    assertThat(catch { tryIo { error("fuck") } }).isNotNull()
  }
}