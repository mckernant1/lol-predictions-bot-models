package com.github.mckernant1.lol.blitzcrank.models

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class UserSettingsAccess(
    ddbClient: DynamoDbClient,
    tableName: String = UserSettings.TABLE_NAME,
) {

    private val enhanced: DynamoDbEnhancedClient = DynamoDbEnhancedClient
        .builder()
        .dynamoDbClient(ddbClient)
        .build()

    private val table = enhanced.table(
        tableName,
        TableSchema.fromClass(UserSettings::class.java)
    )

    fun getSettingsForUser(discordId: String): UserSettings =
        table.getItem(Key.builder().partitionValue(discordId).build())
            ?: UserSettings(discordId = discordId)

    fun putSettings(settings: UserSettings) = table.putItem(settings)

    fun scan(): Sequence<UserSettings> = table.scan().items().asSequence()

}
