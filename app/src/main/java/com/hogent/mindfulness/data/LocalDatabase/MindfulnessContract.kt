package com.hogent.mindfulness.data.LocalDatabase

import android.provider.BaseColumns

class MindfulnessContract {

    class UserEntry:BaseColumns {
        companion object {
            val TABLE_NAME = "user"
            val COLUMN_ID = "_id"
            val COLUMN_UNLOCKED_SESSIONS = "session_ids"
            val COLUMN_CURRENT_SESSION_ID = "current_session_id"
            val COLUMN_CURRENT_EXERCISE_ID = "current_exercise_id"
            val COLUMN_POSTS = "post_ids"
            val COLUMN_FEEDBACK_IS_SUBSCRIBED = "feedback_is_subscribed"
            val COLUMN_EMAIL = "email"
            val COLUMN_FIRSTNAME = "firstname"
            val COLUMN_LASTNAME = "lastname"
            val COLUMN_GROUP_ID = "group_id"
        }
    }

    class GroupEntry:BaseColumns {
        companion object {
            val TABLE_NAME = "mGroup"
            val COLUMN_ID = "_id"
            val COLUMN_NAME = "name"
            val COLUMN_SESSIONMAP_ID = "sessionmap_ids"
        }
    }
}