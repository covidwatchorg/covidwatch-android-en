package org.covidwatch.android.data.converter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant

internal class InstantConverterTest {

    private val instantConverter = InstantConverter()

    @Nested
    inner class ToDate {
        @Test
        fun `Convert Long to Date`() {
            assertThat(instantConverter.toInstant(205465402)).isEqualTo(
                Instant.ofEpochMilli(
                    205465402
                )
            )
        }

        @Test
        fun `Convert Null to Date(Null)`() {
            assertThat(instantConverter.toInstant(null)).isEqualTo(null)
        }
    }

    @Nested
    inner class FromDate {
        @Test
        fun `Convert Date to Long`() {
            assertThat(instantConverter.fromInstant(Instant.ofEpochMilli(205465402))).isEqualTo(
                205465402
            )
        }

        @Test
        fun `Convert Null to Long(Null)`() {
            assertThat(instantConverter.fromInstant(null)).isEqualTo(null)
        }
    }

}