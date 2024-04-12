package com.zibi.client.fragment.setting.domain.model

data class ParamAll(
    var paramApp: ParamApp = ParamApp(),
    var paramClient: ParamClient = ParamClient(),
    var paramClientDefault: ParamClient = ParamClient(),
    var paramBroker: ParamBroker = ParamBroker(),
    var paramBrokerDefault: ParamBroker = ParamBroker(),
)
