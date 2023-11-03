package com.example.bookstores.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.bookstores.Activity.DetailActivity
import com.example.bookstores.Activity.Adapter.RvAdapter
import com.example.bookstores.Activity.Adapter.RvAdapterAds
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.R
import com.example.bookstores.interfaces.onItemClickListener
import com.example.oder_food_app.Adapter.AdsModel
import com.example.oder_food_app.Adapter.PhotoAdapter
import com.example.oder_food_app.Adapter.PhotoModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import me.relex.circleindicator.CircleIndicator3
import java.text.Normalizer

class HomeFragment : Fragment() {

    private lateinit var mView: View
    private lateinit var dbRef: DatabaseReference
    private lateinit var listBook: ArrayList<BookModel>
    private lateinit var listComic: ArrayList<BookModel>
    private lateinit var mListPhoto: ArrayList<PhotoModel>
    private lateinit var circleIndicator3: CircleIndicator3
    private lateinit var viewPaperSlide: ViewPager2
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val scrollDelay = 3000L
    private lateinit var mAdapterBook: RvAdapter
    private lateinit var mAdapterComic: RvAdapter
    private lateinit var filteredListBook: ArrayList<BookModel>
    private lateinit var filteredListComic: ArrayList<BookModel>
    private lateinit var mListAds: ArrayList<AdsModel>
    private var isClickLove : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_home, container, false)

        listBook = arrayListOf<BookModel>()
        listComic = arrayListOf<BookModel>()

        getSach()
        searchView()
        slideViewPager()
        adsAdapter()
        return mView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gán OnClickListener cho SearchView
        view.findViewById<SearchView>(R.id.search_view).setOnClickListener {
            // Hiển thị bàn phím ảo
            showKeyboard()
        }
    }

    private fun getSach(){
        mView.findViewById<RecyclerView>(R.id.rcvbook).visibility = View.GONE
        mView.findViewById<TextView>(R.id.txtLoadingData).visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("BookHome")
        dbRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("CutPasteId")
            override fun onDataChange(snapshot: DataSnapshot) {
                listBook.clear()
                listComic.clear()
                if(snapshot.exists()){
                    for(book in snapshot.children){
                        val bookData = book.getValue(BookModel::class.java)
                        if (bookData?.bkindOfSach != null) {
                            val bkindOfSach = bookData.bkindOfSach.trim().toLowerCase()
                            if (bkindOfSach == "sách") {
                                listBook.add(bookData)
                            }else if(bkindOfSach == "truyện tranh"){
                                listComic.add(bookData)
                            }
                        }
                    }
                    filteredListBook = ArrayList(listBook)
                    filteredListComic = ArrayList(listComic)
                    bookAdapter()
                    comicAdapter()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun searchView() {
        val searchView = mView.findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtererList(newText)
                return true
            }
        })
    }

    private fun filtererList(query: String?) {
        if (query != null) {
            //chuyển hóa chuỗi loại bỏ dấu để so sánh
            val normalizedQuery = removeDiacritics(query.toLowerCase())
            filteredListBook = ArrayList()
            filteredListComic = ArrayList()
            for (i in listBook) {
                val normalizedTitle = i.btitle?.let { removeDiacritics(it.toLowerCase()) }
                if (normalizedTitle != null) {
                    if (normalizedTitle.contains(normalizedQuery)) {
                        filteredListBook.add(i)
                    }
                }
            }
            for (i in listComic) {
                val normalizedTitle = i.btitle?.let { removeDiacritics(it.toLowerCase()) }
                if (normalizedTitle != null) {
                    if (normalizedTitle.contains(normalizedQuery)) {
                        filteredListComic.add(i)
                    }
                }
            }
            mAdapterBook.clearData()
            mAdapterBook.setFilteredList(filteredListBook)
            mAdapterComic.clearData()
            mAdapterComic.setFilteredList(filteredListComic)

        }
    }

    private fun removeDiacritics(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("[^\\p{ASCII}]".toRegex(), "")
    }
    @SuppressLint("CutPasteId")
    private fun bookAdapter(){
        mAdapterBook = RvAdapter(listBook)
        mView.findViewById<RecyclerView>(R.id.rcvbook).adapter = mAdapterBook
        mView.findViewById<RecyclerView>(R.id.rcvbook).layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL,false)
        mView.findViewById<RecyclerView>(R.id.rcvbook).visibility = View.VISIBLE
        //code lắng nghe sự kiện
        mAdapterBook.setOnItemClickListener(object : onItemClickListener {
            override fun onItemClick(position: Int) {
                val clickedFood = filteredListBook[position]
                val originalPosition = listBook.indexOf(clickedFood)
                val intent = Intent(context, DetailActivity::class.java )
                val bundle = Bundle()
                val bookList = ArrayList<Parcelable>(listBook)
                bundle.putParcelableArrayList("bookList", bookList)
                bundle.putInt("pos", originalPosition)
                intent.putExtras(bundle)
                startActivity(intent)
                val fragmentTransaction = parentFragmentManager.beginTransaction()

            }
        })
        mView.findViewById<TextView>(R.id.txtLoadingData).visibility = View.GONE
    }
    @SuppressLint("CutPasteId")
    private fun comicAdapter(){
        mAdapterComic = RvAdapter(listComic)
        mView.findViewById<RecyclerView>(R.id.rcvtale).adapter = mAdapterComic
        mView.findViewById<RecyclerView>(R.id.rcvtale).layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL,false)
        //code lắng nghe sự kiện
        mAdapterComic.setOnItemClickListener(object : onItemClickListener {
            override fun onItemClick(position: Int) {
                val clickedFood = filteredListComic[position]
                val originalPosition = listComic.indexOf(clickedFood)
                val intent = Intent(requireActivity(), DetailActivity::class.java )
                val bundle = Bundle()
                val bookList = ArrayList<Parcelable>(listComic)
                bundle.putParcelableArrayList("bookList", bookList)
                bundle.putInt("pos", originalPosition)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
    }
    private fun showKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view?.findViewById(R.id.search_view), InputMethodManager.SHOW_IMPLICIT)
    }
    private fun slideViewPager(){
        viewPaperSlide = mView.findViewById(R.id.view_paper_slide)
        circleIndicator3 = mView.findViewById(R.id.circle_indicator)
        mListPhoto = getListPhoto() as ArrayList<PhotoModel>
        val adapterphoto = PhotoAdapter(mListPhoto)
        viewPaperSlide.adapter = adapterphoto
        circleIndicator3.setViewPager(viewPaperSlide)
        //setting
        viewPaperSlide.offscreenPageLimit = 3
        viewPaperSlide.clipToPadding = false
        viewPaperSlide.clipChildren = false
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(0))
        compositePageTransformer.addTransformer { page, position ->
            val scale: Float
            if (position < -1) { // Khi trang không hiển thị
                scale = 0.85f // Đặt kích thước nhỏ
            } else if (position <= 1) { // Khi trang hiển thị (trái và phải)
                scale = 0.85f + (1 - Math.abs(position)) * 0.15f // Tính toán kích thước dựa trên vị trí
            } else { // Khi trang không hiển thị
                scale = 0.85f // Đặt kích thước nhỏ
            }
            // Đặt thuộc tính scaleX và scaleY của trang
            page.scaleX = scale
            page.scaleY = scale
        }
        viewPaperSlide.setPageTransformer(compositePageTransformer)
        startAutoScroll()
    }
    @SuppressLint("CutPasteId")
    private fun adsAdapter(){
        val adapter = RvAdapterAds(getListAds() as ArrayList<AdsModel>)
        mView.findViewById<RecyclerView>(R.id.rcvAds).adapter = adapter
        mView.findViewById<RecyclerView>(R.id.rcvAds).layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        adapter.setOnItemClickListener(object : onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(context, "Bạn đã click vào ${getListAds()[position].title}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun startAutoScroll() {
        autoScrollHandler.postDelayed(object : Runnable {
            override fun run() {
                if (currentPage < getListPhoto().size) {
                    viewPaperSlide.currentItem = currentPage
                    currentPage++
                } else {
                    viewPaperSlide.currentItem = 0
                    currentPage = 0
                }

                autoScrollHandler.postDelayed(this, scrollDelay)
            }
        }, scrollDelay)
    }
    private fun getListPhoto(): List<PhotoModel> {
        val list: MutableList<PhotoModel> = ArrayList()
        list.add(PhotoModel(R.drawable.img_slide1))
        list.add(PhotoModel(R.drawable.img_slide2))
        list.add(PhotoModel(R.drawable.img_slide3))
        list.add(PhotoModel(R.drawable.img_slide4))
        return list
    }
    private fun getListAds(): List<AdsModel> {
        val list: MutableList<AdsModel> = ArrayList()
        list.add(AdsModel(R.drawable.img_freeship, "Miễn phí vận chuyển"))
        list.add(AdsModel(R.drawable.img_sale, "Mã giảm giá"))
        list.add(AdsModel(R.drawable.ic_calendar, "Vận chuyển 24h"))
        list.add(AdsModel(R.drawable.ic_fire, "Hot"))
        list.add(AdsModel(R.drawable.ic_smartphone, "Nạp thẻ"))
        list.add(AdsModel(R.drawable.ic_more_horiz, "See more"))

        return list
    }
}