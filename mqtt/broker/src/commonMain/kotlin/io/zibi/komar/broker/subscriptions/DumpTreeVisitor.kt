package io.zibi.komar.broker.subscriptions

import io.zibi.komar.broker.subscriptions.CTrie.IVisitor

internal class DumpTreeVisitor : IVisitor<String> {
    override var result = ""
    override fun visit(node: CNode, deep: Int) {
        val indentTabs = indentTabs(deep)
        result += indentTabs + (if (node.token == null) "''" else node.token.toString()) + prettySubscriptions(
            node
        ) + "\n"
    }

    private fun prettySubscriptions(node: CNode): String {
        if (node is TNode) {
            return "TNode"
        }
        if (node.subscriptions.isEmpty()) {
            return ""
        }
        val subScriptionsStr = StringBuilder(" ~~[")
        var counter = 0
        for (couple in node.subscriptions) {
            subScriptionsStr
                .append("{filter=").append(couple.topicFilter).append(", ")
                .append("qos=").append(couple.requestedQos).append(", ")
                .append("client='").append(couple.clientId).append("'}")
            counter++
            if (counter < node.subscriptions.size) {
                subScriptionsStr.append(";")
            }
        }
        return subScriptionsStr.append("]").toString()
    }

    private fun indentTabs(deep: Int): String {
        val s = StringBuilder()
        if (deep > 0) {
            s.append("    ")
            for (i in 0 until deep - 1) {
                s.append("| ")
            }
            s.append("|-")
        }
        return s.toString()
    }
}
