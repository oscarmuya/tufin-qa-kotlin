package org.example

data class FirewallRule(
    val name: String, val sourceRange: String, val port: Int, val protocol: String
);

fun FirewallRule.matches(sourceIp: String?, port: Int, protocol: String?): Boolean {
    return (
            this.port == port
                    // protocol matching is case-insensitive
                    && this.protocol.equals(protocol, ignoreCase = true)
                    // the rule's source is either a wildcard or the exact IP
                    // if sourceIP is null that still matches to wildcard
                    && (this.sourceRange == "*" || this.sourceRange == sourceIp))
}
