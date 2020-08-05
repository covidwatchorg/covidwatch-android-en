package org.covidwatch.android.data.converter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertNull

internal class InstantConverterTest {

    private val instantConverter = InstantConverter()

    @Nested
    inner class ToInstant {
        @Test
        fun `Convert Long to Instant`() {
            assertThat(instantConverter.toInstant(205465402)).isEqualTo(
                Instant.ofEpochMilli(
                    205465402
                )
            )
        }

        @Test
        fun `Convert Null to Instant(Null)`() {
            assertNull(instantConverter.toInstant(null))
        }
    }

    @Nested
    inner class FromInstant {
        @Test
        fun `Convert Instant to Long`() {
            assertThat(instantConverter.fromInstant(Instant.ofEpochMilli(205465402))).isEqualTo(
                205465402
            )
        }

        @Test
        fun `Convert Null to Long(Null)`() {
            assertNull(instantConverter.fromInstant(null))
        }
    }

}