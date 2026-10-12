package org.example

class PolicyUtils {
    companion object {
        fun shouldApplyRule(
            sourceIp: String?,
            destinationPort: Int,
            protocol: String?,
            rules: List<FirewallRule>
        ): Boolean {
            return rules.any { it.matches(sourceIp, destinationPort, protocol) }
        }
    }
}
