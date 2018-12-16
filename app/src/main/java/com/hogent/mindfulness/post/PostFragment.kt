package com.hogent.mindfulness.post


import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import com.hogent.mindfulness.R.drawable.tweedetestfoto
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.PostViewModel
import com.hogent.mindfulness.exercise_details.MultipleChoiceItemAdapter
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.post_view.view.*
import org.jetbrains.anko.imageResource
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 *
 */
class PostFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var postView: PostViewModel

    /*
     * In the onCreate method we initialize the postViewModel, so we can retrieve the user's posts.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postView = activity?.run {
            ViewModelProviders.of(this).get(PostViewModel::class.java)
        } ?: throw Exception("Invalid activity.")
        postView.retrievePosts()

    }

    /*
     * In the onCreateView method we inflate the fragment's layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    /*
     * In the onViewCreated method we retrieve the user's posts, initialize the layoutmanager and the RecyclerView
     * with the adapter.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postView.posts.observe(this, Observer {
            if (it == null || it.isEmpty()) {
                emptyPostsLayout.visibility = View.VISIBLE
                post_recycler_view.visibility = View.GONE
            } else {
                emptyPostsLayout.visibility = View.GONE
                post_recycler_view.visibility = View.VISIBLE
            }
        })

        viewManager = LinearLayoutManager((activity as MainActivity))
        viewAdapter = PostAdapter(postView, this, (activity as MainActivity))
        post_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        //postService = ServiceGenerator.createService(PostApiService::class.java, (activity as MainActivity))
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle("Tijdlijn")
    }
}


/***********************************************************************************************
 * Adapter
 *
 ***********************************************************************************************
 */
class PostAdapter(
    private var viewModel: PostViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val activity: MainActivity
) : RecyclerView.Adapter<PostAdapter.viewHolder>() {

    class viewHolder(val postView: CardView) : RecyclerView.ViewHolder(postView)

    private var dataSet: Array<Model.Post> = arrayOf()

    init {
        Log.d("POSTIE_WOSTIES", "ADAPTER_CREATION_CHECK")
        if (viewModel.posts.value != null) {
            dataSet = viewModel.posts.value!!
        }
        viewModel.posts.observe(lifecycleOwner, Observer {
            Log.d("POSTIE_WOSTIES", "CHECK")
            if (it != null) {
                Log.d("POSTIE_WOSTIES", "${it}")
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
        if (dataSet[position].image_file_name != null) {
            holder.setIsRecyclable(false)
            if (viewModel.bitHashMap.containsKey(dataSet[position].image_file_name))
                holder.postView.post_card_image.setImageBitmap(viewModel.bitHashMap.get(dataSet[position].image_file_name))
            else
                holder.postView.post_card_image.imageResource = tweedetestfoto
            holder.postView.post_desc.visibility = View.GONE
            holder.postView.post_card_list.visibility = View.GONE
        } else if (dataSet[position].inhoud != null) {
            holder.postView.post_card_image.visibility = View.GONE
            holder.postView.post_card_list.visibility = View.GONE
            holder.postView.post_desc.text = dataSet[position].inhoud
        } else {
            holder.postView.post_card_image.visibility = View.GONE
            holder.postView.post_desc.visibility = View.GONE
            holder.postView.post_card_list.apply {
                adapter = MultipleChoiceItemAdapter(dataSet[position].multiple_choice_items, false)
                layoutManager = LinearLayoutManager(activity)
            }
        }

        val format = SimpleDateFormat("dd-MMM-yyyy  hh:mm a")
        if (dataSet[position].date != null)
            holder.postView.post_date.text = format.format(dataSet[position].date)
    }

}

