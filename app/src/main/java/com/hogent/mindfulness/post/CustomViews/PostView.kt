package com.hogent.mindfulness.post.CustomViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.hogent.mindfulness.R

class PostView(context: Context, attrs:AttributeSet):LinearLayout(context, attrs) {
     init {
          View.inflate(context, R.layout.post_view, this)
     }
}