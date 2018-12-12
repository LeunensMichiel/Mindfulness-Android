package com.hogent.mindfulness.post


import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PostViewModel
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.post_view.view.*
import org.jetbrains.anko.imageBitmap
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 *
 */
class PostFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var postView:PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postView = activity?.run {
            ViewModelProviders.of(this).get(PostViewModel::class.java)
        }?: throw Exception("Invalid activity.")
        postView.retrievePosts()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager((activity as MainActivity))
        viewAdapter = PostAdapter(postView, this)
        post_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            swapAdapter(viewAdapter, false)
        }
        //postService = ServiceGenerator.createService(PostApiService::class.java, (activity as MainActivity))
    }
}

class PostAdapter(private var viewModel: PostViewModel,
                  private val lifecycleOwner: LifecycleOwner)
    : RecyclerView.Adapter<PostAdapter.viewHolder>() {

    class viewHolder(val postView: CardView) : RecyclerView.ViewHolder(postView)
    private var dataSet: Array<Model.Post> = arrayOf()

    init {
        if (viewModel.posts.value != null){
            dataSet = viewModel.posts.value!!
        }
        viewModel.posts.observe(lifecycleOwner, Observer {
            if (it != null){
                dataSet = it
                notifyDataSetChanged()
            } else {
                dataSet = arrayOf()
                notifyDataSetChanged()
            }
        })
    }


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

