package com.github.mckernant1.lol.blitzcrank.models

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@DynamoDbBean
class UserSettings(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("discordId") var discordId: String? = null,
    var timezone: String = "America/Los_Angeles",
    var pasta: String = "You have no pasta. Set one with `!setPasta My Pasta!`",
    @get:DynamoDbConvertedBy(ReminderListConverter::class)
    var reminders: MutableList<Reminder> = mutableListOf()
) {

    companion object {
        const val TABLE_NAME = "user-settings"
    }

    override fun toString(): String = "[discordId=$discordId, timezone=$timezone, pasta=$pasta, reminders=$reminders]"
}

@DynamoDbBean
class Reminder(
    val userId: String = "12345678",
    val leagueSlug: String = "lcs",
    val hoursBeforeMatches: Long = 0,
    var lastReminderSentEpochMillis: Long = 0
) {
    override fun toString(): String =
        "{userId: $userId, leagueSlug: $leagueSlug, hoursBeforeMatches: $hoursBeforeMatches}"
}


class ReminderListConverter : AttributeConverter<List<Reminder>> {

    companion object {
        private val mapper = ObjectMapper()
    }

    override fun transformFrom(input: List<Reminder>?): AttributeValue {
        return AttributeValue.builder().s(mapper.writeValueAsString(input)).build()
    }

    override fun transformTo(input: AttributeValue?): List<Reminder> {
        return mapper.readValue<List<Reminder>>(input?.s(), object : TypeReference<List<Reminder>?>() {})
    }

    override fun type(): EnhancedType<List<Reminder>> {
        return EnhancedType.listOf(Reminder::class.java)
    }

    override fun attributeValueType(): AttributeValueType {
        return AttributeValueType.S
    }

}
