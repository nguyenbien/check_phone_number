package com.biennt.phone.number.check_phone_number

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.util.*
import kotlin.collections.HashMap


class CheckPhoneNumber {
    private val util: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    fun validatePhone(call: MethodCall, result: MethodChannel.Result){
        val res = mutableMapOf<String, Boolean>()
        val region = call.argument<String>("region")
        val number = call.argument<String>("phone")

        if (number.isNullOrEmpty() || region.isNullOrEmpty()) {
            res["isValid"] = false
        }else{
            try {
                val phoneNumber = util.parse(number, region)
                val isValid = util.isValidNumberForRegion(phoneNumber, region)
                res["isValid"] = isValid
            } catch (exception: Exception) {
                res["isValid"] = false
            }
        }

        result.success(res)
    }


    fun getAllSupportedRegions(call: MethodCall, result: MethodChannel.Result) {
        val map: MutableList<Map<String, Any>> = ArrayList()
        val locale: Locale
        val identifier = call.argument<String>("locale")
        locale = identifier?.let { Locale(it) } ?: Locale.getDefault()
        for (region in util.supportedRegions) {
            val res: MutableMap<String, Any> = HashMap()
            res["name"] = Locale("", region).getDisplayCountry(locale)
            res["code"] = region
            res["prefix"] = PhoneNumberUtil.getInstance().getCountryCodeForRegion(region)
            map.add(res)
        }
        result.success(map)
    }

    fun format(call: MethodCall, result: MethodChannel.Result) {
        val res = mutableMapOf<String, String>()
        val region = call.argument<String>("region")
        val number = call.argument<String>("phone")
        when {
            number.isNullOrEmpty() -> {
                res["formatted"] = ""
            }
            region.isNullOrEmpty() -> {
                res["formatted"] = number
            }
            else -> {
                try {
                    val formatter = util.getAsYouTypeFormatter(region)
                    var formatted = ""
                    formatter.clear()
                    for (character in number.toCharArray()) {
                        formatted = formatter.inputDigit(character)
                    }
                    res["formatted"] = formatted
                } catch (exception: Exception) {
                    res["formatted"] = number
                }

                result.success(res)
            }
        }
    }

    private fun numberTypeToString(type: PhoneNumberType): String {
        return when (type) {
            PhoneNumberType.FIXED_LINE -> "fixedLine"
            PhoneNumberType.MOBILE -> "mobile"
            PhoneNumberType.FIXED_LINE_OR_MOBILE -> "fixedOrMobile"
            PhoneNumberType.TOLL_FREE -> "tollFree"
            PhoneNumberType.PREMIUM_RATE -> "premiumRate"
            PhoneNumberType.SHARED_COST -> "sharedCost"
            PhoneNumberType.VOIP -> "voip"
            PhoneNumberType.PERSONAL_NUMBER -> "personalNumber"
            PhoneNumberType.PAGER -> "pager"
            PhoneNumberType.UAN -> "uan"
            PhoneNumberType.VOICEMAIL -> "voicemail"
            PhoneNumberType.UNKNOWN -> "unknown"
            else -> "notParsed"
        }
    }


    private fun parseStringAndRegion(
        phone: String, region: String
    ): Map<String, String>? {
        try {
            val phoneNumber = util.parse(phone, region)
            return if(util.isValidNumber(phoneNumber)){
                val res = mutableMapOf<String, String>()
                val type = util.getNumberType(phoneNumber)
                res["type"] = numberTypeToString(type)
                res.put("e164", util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164))
                res["international"] = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                res["national"] = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
                res["country_code"] = phoneNumber.countryCode.toString()
                res["national_number"] = phoneNumber.nationalNumber.toString()
                res
            }else{
                null
            }
        }catch (e: NumberParseException){
            return null
        }
    }

    fun parse(call: MethodCall, result: MethodChannel.Result) {
        val region = call.argument<String>("region")
        val phone = call.argument<String>("phone")
        when {
            phone.isNullOrEmpty() -> {
                result.error("InvalidParameters", "Invalid 'phone' parameter.", null)
            }
            region.isNullOrEmpty() -> {
                result.error("InvalidParameters", "Invalid 'region' parameter.", null)
            } else -> {
                val res = parseStringAndRegion(phone, region)
                if (res != null) {
                    result.success(res)
                } else {
                    result.error("InvalidNumber", "Number $phone is invalid", null)
                }
            }
        }
    }

    fun parseList(call: MethodCall, result: MethodChannel.Result) {
        val region = call.argument<String>("region")
        val phones = call.argument<List<String>>("phones")
        when {
            phones.isNullOrEmpty() -> {
                result.error("InvalidParameters", "Invalid 'phones' parameter.", null)
            }
            region.isNullOrEmpty() -> {
                result.error("InvalidParameters", "Invalid 'region' parameter.", null)
            }
            else -> {
                val res = mutableMapOf<String, Map<String, String>?>()
                for (string in phones) {
                    val stringResult = parseStringAndRegion(
                        string,
                        region
                    )

                    res[string] = stringResult
                }
                result.success(res)
            }
        }
    }

}