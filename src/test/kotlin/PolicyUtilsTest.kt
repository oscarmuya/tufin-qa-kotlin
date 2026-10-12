import org.example.FirewallRule
import org.example.PolicyUtils
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


fun aRule(
    name: String = "Allow-SSH",
    sourceRange: String = "*",
    port: Int = 22,
    protocol: String = "tcp"
) = FirewallRule(name, sourceRange, port, protocol)

class PolicyUtilsTest {

    private fun shouldApply(
        rules: List<FirewallRule>,
        sourceIp: String? = "10.0.0.5",
        port: Int = 22,
        protocol: String? = "tcp"
    ) = PolicyUtils.shouldApplyRule(sourceIp, port, protocol, rules)

    @Test
    fun `returns false when there are no rules`() {
        val rules = emptyList<FirewallRule>()
        assertFalse(shouldApply(rules), "expected no match with an empty rule list")
    }

    @Test
    fun `matches the default rule`() {
        val rules = listOf(aRule())
        assertTrue(shouldApply(rules), "expected $rules to match 10.0.0.5:22/tcp")
    }

    @Test
    fun `wildcard source range matches any source ip`() {
        val rules = listOf(aRule(sourceRange = "*"))
        val sourceIp = "203.0.113.9"
        assertTrue(shouldApply(rules, sourceIp = sourceIp), "expected $rules to match $sourceIp")
    }

    @Test
    fun `exact source range matches the same ip`() {
        val rules = listOf(aRule(sourceRange = "10.0.0.5"))
        val sourceIp = "10.0.0.5"
        assertTrue(shouldApply(rules, sourceIp = sourceIp), "expected $rules to match $sourceIp")
    }

    @Test
    fun `exact source range rejects a different ip`() {
        val rules = listOf(aRule(sourceRange = "10.0.0.5"))
        val sourceIp = "10.0.0.6"
        assertFalse(shouldApply(rules, sourceIp = sourceIp), "expected $rules not to match $sourceIp")
    }

    @Test
    fun `rejects a different port`() {
        val rules = listOf(aRule(port = 22))
        val port = 443
        assertFalse(shouldApply(rules, port = port), "expected $rules not to match port $port")
    }

    @Test
    fun `rejects a different protocol`() {
        val rules = listOf(aRule(protocol = "tcp"))
        val protocol = "udp"
        assertFalse(shouldApply(rules, protocol = protocol), "expected $rules not to match protocol $protocol")
    }

    @Test
    fun `protocol comparison ignores case`() {
        val rules = listOf(aRule(protocol = "tcp"))
        val protocol = "TCP"
        assertTrue(shouldApply(rules, protocol = protocol), "expected $rules to match protocol $protocol")
    }

    @Test
    fun `null source ip matches a wildcard rule`() {
        val rules = listOf(aRule(sourceRange = "*"))
        assertTrue(shouldApply(rules, sourceIp = null), "expected $rules to match a null source ip")
    }

    @Test
    fun `null source ip does not match an exact source rule`() {
        val rules = listOf(aRule(sourceRange = "10.0.0.5"))
        assertFalse(shouldApply(rules, sourceIp = null), "expected $rules not to match a null source ip")
    }

    @Test
    fun `null protocol never matches`() {
        val rules = listOf(aRule())
        assertFalse(shouldApply(rules, protocol = null), "expected $rules not to match a null protocol")
    }

    @Test
    fun `returns true when only one of several rules matches`() {
        val rules = listOf(
            aRule(name = "Allow-HTTPS", port = 443),
            aRule(name = "Allow-DNS", port = 53, protocol = "udp"),
            aRule(name = "Allow-SSH")
        )
        assertTrue(shouldApply(rules, port = 22), "expected one of $rules to match port 22")
    }

    @Test
    fun `returns false when none of several rules match`() {
        val rules = listOf(
            aRule(name = "Allow-HTTPS", port = 443),
            aRule(name = "Allow-DNS", port = 53, protocol = "udp")
        )
        assertFalse(shouldApply(rules, port = 22), "expected none of $rules to match port 22")
    }
}
