package com.hogent.mindfulness.post


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.hogent.mindfulness.MainActivity

import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.data.PostApiService
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.post.CustomViews.PostView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.post_view.view.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

/**
 * A simple [Fragment] subclass.
 *
 */
class PostFragment : Fragment() {

    private var postService:PostApiService? by observable(null) {property, oldValue:PostApiService?, newValue:PostApiService? ->
        dbUser = mMindfulDB.getUser()!!
        disposable = newValue!!.getPosts(dbUser._id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> initializeRecyclerView(result) },
                { error -> Log.i("fuck", "$error")}
            )
    }
    private val mMindfulDB by lazy {
        MindfulnessDBHelper((activity as MainActivity))
    }
    private var posts: Array<Model.Post> = arrayOf()
    private lateinit var dbUser:Model.User
    private lateinit var disposable: Disposable
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postService = ServiceGenerator.createService(PostApiService::class.java, (activity as MainActivity))
    }

    fun initializeRecyclerView(newPosts:Array<Model.Post>){
        posts = newPosts
        if (posts.isNotEmpty()){
            viewManager = LinearLayoutManager((activity as MainActivity))
            viewAdapter = PostAdapter(posts, (activity as MainActivity))
            post_recycler_view.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                swapAdapter(viewAdapter, false)
            }
        }
    }

}

class PostAdapter(private val dataSet: Array<Model.Post>, context:Context) : RecyclerView.Adapter<PostAdapter.viewHolder>() {

    class viewHolder(val postView: CardView) : RecyclerView.ViewHolder(postView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.viewHolder {
        val postView = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_view, parent, false) as CardView
        return viewHolder(postView)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: PostAdapter.viewHolder, position: Int) {

        holder.postView.post_session_name.text = dataSet[position].session_name
        holder.postView.post_ex_name.text = dataSet[position].exercise_name
        holder.postView.post_desc.text = dataSet[position].inhoud
    }

}

