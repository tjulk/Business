LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := jdom apache commons httpmime nets

LOCAL_MODULE_TAGS := eng

LOCAL_SRC_FILES := $(call all-subdir-java-files) \

LOCAL_PACKAGE_NAME := XiangMinBusiness
LOCAL_AAPT_FLAGS += -c hdpi

include $(BUILD_PACKAGE)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := jdom:libs/jdom-2.0.2.jar \
apache:libs/apache-mime4j-0.6.jar  \
commons:libs/commons-io-2.0.1.jar \
httpmime:libs/httpmime-4.1.1.jar \
nets:libs/netspeechapi.jar
   
include $(BUILD_MULTI_PREBUILT)
