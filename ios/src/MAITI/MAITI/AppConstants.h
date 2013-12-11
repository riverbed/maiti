/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/

#pragma once

//SDK Current Version 
#define SDK_VERSION 1

#define SEND_BUFFER_TIMER_INTERVAL_SEC 5.0

//Device Agent
//ios for IOS, droid for Android
#define AGENT_TYPE                  @"ios"
#define INTERVAL_TRANS_NAME         @"interval"
#define NOTIFICATION_TRANS_NAME     @"notification"

#define NETWORK_WIFI_KEY            @"wifi"
#define NETWORK_3G_KEY              @"3g"
#define NETWORK_4G_KEY              @"4g"
#define NETWORK_NOT_AVAILABLE_KEY   @"none"
#define NETWORK_UNKNOWN_KEY         @"wifi"

#define PARENT_OFFSET_KEY          @"_opParOfst"

#define PREF_HOST           @"DataCollectorHost"
#define PREF_USE_HTTPS      @"UseHTTPS"
#define PREF_PORT           @"DataCollectorPort"
#define PREF_ENABLED        @"Enabled"
#define PREF_RECORD_CONN    @"RecordConnectionType"
#define PREF_RECORD_MEM     @"RecordMemory"
#define PREF_RECORD_SERIAL  @"RecordSerial"

#define JSONKEY_MAITI_VERSION       @"ver"
#define JSONKEY_STARTTIME           @"start_time"
#define JSONKEY_DURATION            @"dur"
#define JSONKEY_ENDTIME             @"end_time"
#define JSONKEY_USERTAG1            @"utag1"
#define JSONKEY_USERTAG2            @"utag2"
#define JSONKEY_USERTAG3            @"utag3"
#define JSONKEY_USERDATA            @"udata"
#define JSONKEY_TRANSACTION_NAME    @"name"
#define JSONKEY_PARENT_ID           @"parent_id"
#define JSONKEY_CUSTOMER_ID         @"cust_id"
#define JSONKEY_SESSION_ID          @"sess_id"
#define JSONKEY_APP_ID              @"app_id"
#define JSONKEY_TRANSACTION_ID      @"id"
#define JSONKEY_TRANSACTION_TYPE    @"type"
#define JSONKEY_AGENT_TYPE          @"agent_type"
#define JSONKEY_OS_VERSION          @"os"
#define JSONKEY_HW_MODEL            @"model"
#define JSONKEY_PACKAGE_ID          @"pkg_id"
#define JSONKEY_CODE_VER            @"code_ver"
#define JSONKEY_MEM_TOTAL           @"mem_total"
#define JSONKEY_MEM_FREE            @"mem_free"
#define JSONKEY_ERROR               @"error"
#define JSONKEY_HW_SERIAL_NUMBER    @"ser_num"
#define JSONKEY_CONNECTION_TYPE     @"conn"