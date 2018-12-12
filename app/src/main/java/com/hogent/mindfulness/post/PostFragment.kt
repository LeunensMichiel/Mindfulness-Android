package com.hogent.mindfulness.post


import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.FIleApiService
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.data.PostApiService
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.post_view.view.*
import okhttp3.ResponseBody
import org.jetbrains.anko.imageBitmap
import java.io.File
import java.io.FileOutputStream
import kotlin.properties.Delegates.observable

/**
 * A simple [Fragment] subclass.
 *
 */
class PostFragment : Fragment() {

    private var postService: PostApiService? by observable(null) { property, oldValue: PostApiService?, newValue: PostApiService? ->
        dbUser = mMindfulDB.getUser()!!
        disposable = newValue!!.getPosts(dbUser._id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> initializeRecyclerView(result) },
                { error -> Log.i("postApiError", "$error") }
            )
    }
    private lateinit var fileService: FIleApiService

    private val mMindfulDB by lazy {
        MindfulnessDBHelper((activity as MainActivity))
    }
    private var posts: Array<Model.Post> = arrayOf()
    private lateinit var dbUser: Model.User
    private lateinit var disposable: Disposable
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fileService = ServiceGenerator.createService(FIleApiService::class.java, (activity as MainActivity))
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postService = ServiceGenerator.createService(PostApiService::class.java, (activity as MainActivity))
    }

    private fun initializeRecyclerView(newPosts: Array<Model.Post>) {
        posts = newPosts
        if (posts.isNotEmpty()) {
            loadImages()
            viewManager = LinearLayoutManager((activity as MainActivity))
            viewAdapter = PostAdapter(posts, (activity as MainActivity))
            post_recycler_view.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                swapAdapter(viewAdapter, false)
            }
        }
    }

    private fun loadImages() {
        posts.
            forEachIndexed { i, post ->
                if (post.image_file_name != null) {
                    disposable = fileService.getFile("post_image", post.image_file_name!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { result -> convertToBitmap(result, post.image_file_name!!, i) },
                            { error -> Log.i("EXERCISE ERROR", "$error") }
                        )
                }
            }
    }

    private fun convertToBitmap(result: ResponseBody, fileName: String, position: Int) {
        var imgFile = File.createTempFile(fileName, "png")
        imgFile.deleteOnExit()
        val fos = FileOutputStream(imgFile)
        fos.write(result.bytes())
        posts[position].bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
    }

}

class PostAdapter(private val dataSet: Array<Model.Post>, context: Context) :
    RecyclerView.Adapter<PostAdapter.viewHolder>() {

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

        if (dataSet[position].bitmap != null) {
            holder.postView.post_card_image.imageBitmap = dataSet[position].bitmap
        } else {
            holder.postView.post_card_image.visibility = View.GONE
        }
        
        if (dataSet[position].inhoud != null) {
            holder.postView.post_desc.text = dataSet[position].inhoud
        } else {
            holder.postView.post_desc.visibility = View.GONE
        }
    }

}

