package com.example.bookstores.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.bookstores.Fragment.CartFragment
import com.example.bookstores.Fragment.FavoriteFragment
import com.example.bookstores.Fragment.HistoryFragment
import com.example.bookstores.Fragment.HomeFragment
import com.example.bookstores.interfaces.Model.BookModel
import com.example.bookstores.R
import com.example.bookstores.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: Dialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alertDialog = AlertDialog.Builder(this)
        val progressBar = ProgressBar(this)

        alertDialog.setView(progressBar)
        alertDialog.setTitle("Đang đăng xuất !")
        alertDialog.setCancelable(false)
        dialog = alertDialog.create()

        binding.txtClearAllFavourite.visibility = View.GONE
        binding.txtClearAllCart.visibility = View.GONE
        binding.txtClearAllHistory.visibility = View.GONE

        val intent = intent
        val toast = intent.getStringExtra("Login")
        val email = intent.getStringExtra("email")
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
        val navigationView = findViewById<NavigationView>(R.id.navigation_drawer)
        val headerView = navigationView.getHeaderView(0)
        val emailTextView = headerView.findViewById<TextView>(R.id.txtemail)
        emailTextView.text = email.toString()

        //bottom navigation
        binding.btNavigation.setOnItemSelectedListener {  item ->
            when(item.itemId){
                R.id.bt_home -> {
                    openFragment(HomeFragment())
                    findViewById<TextView>(R.id.txtbook).text = "BookStores"
                    binding.txtClearAllFavourite.visibility = View.GONE
                    binding.txtClearAllCart.visibility = View.GONE
                    binding.txtClearAllHistory.visibility = View.GONE
                }
                R.id.bt_cart -> {
                    openFragment(CartFragment())
                    findViewById<TextView>(R.id.txtbook).text = "Giỏ hàng"
                    binding.txtClearAllCart.visibility = View.VISIBLE
                    binding.txtClearAllFavourite.visibility = View.GONE
                    binding.txtClearAllHistory.visibility = View.GONE
                }
                R.id.bt_history -> {
                    openFragment(HistoryFragment())
                    findViewById<TextView>(R.id.txtbook).text = "Lịch sử"
                    binding.txtClearAllHistory.visibility = View.VISIBLE
                    binding.txtClearAllFavourite.visibility = View.GONE
                    binding.txtClearAllCart.visibility = View.GONE
                }
                R.id.bt_favourite -> {
                    openFragment(FavoriteFragment())
                    findViewById<TextView>(R.id.txtbook).text = "Yêu thích"
                    binding.txtClearAllFavourite.visibility = View.VISIBLE
                    binding.txtClearAllHistory.visibility = View.GONE
                    binding.txtClearAllCart.visibility = View.GONE
                }
            }
            true
        }
        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())
//        binding.fab.setOnClickListener {
//            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()
//        }
        addDatta()
        btnimgNavigation()
        Navigation()
    }

    override fun onBackPressed(){
        if(binding.drawLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawLayout.closeDrawer(GravityCompat.START)
        }else{
            super.getOnBackPressedDispatcher().onBackPressed()
        }
    }
    private fun Navigation(){
        findViewById<NavigationView>(R.id.navigation_drawer).setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> openFragment(HomeFragment())
                R.id.nav_out -> {
                    dialog.show()
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }, 1500)
                }
            }
            binding.drawLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    private fun btnimgNavigation(){
        val drawerLayout = findViewById<DrawerLayout>(R.id.draw_layout)
        val navView = findViewById<NavigationView>(R.id.navigation_drawer)
        binding.imgNav.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }
    }
    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
    fun txtClearCart(): TextView {
        val txtClearCart = findViewById<TextView>(R.id.txtClearAllCart)
        return txtClearCart
    }

    fun txtClearHistory(): TextView {
        val txtClearHistory = findViewById<TextView>(R.id.txtClearAllHistory)
        return txtClearHistory
    }

    fun txtClearFavourite(): TextView {
        val txtClearFavourite = findViewById<TextView>(R.id.txtClearAllFavourite)
        return txtClearFavourite
    }

    private fun addDatta(){
        val database = Firebase.database
        val myRef = database.getReference("BookHome")

        val books = listOf(
            BookModel(null, "Đắc Nhân Tâm","https://th.bing.com/th/id/OIP.ZiwfBrifIO4lV_Q-gIC7VQHaKx?w=119&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Dale Carnegie","Tổng hợp Thành Phố Hồ Chí Minh", "319 trang","Sách",80.000,"Đắc Nhân Tâm là cuốn sách nói về cách sống để được lòng người khác1. Cuốn sách giúp người đọc hiểu rõ bản thân, thành thật với những suy nghĩ, ước muốn của mình, và từ đó có thể dành sự quan tâm, chân thành đến với những người xung quanh. Ngoài ra, cuốn sách còn giúp người đọc cách đề phòng và giải quyết những khúc mắc, hiểu lầm từ nhỏ nhặt cho đến nghiêm trong trong vấn đề giao tiếp và ứng xử một cách chuyên nghiệp và tinh tế nhất trong bất kỳ tình huống và trường hợp nào."),
            BookModel(null, "Nhà Lãnh Đạo 360 Độ","https://th.bing.com/th/id/OIP.UDAaotE0Evwlyu_1toYcXgAAAA?w=115&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "John C. Maxwell","Lao Động","400 trang","Sách",70.000,"Trong cuốn Nhà lãnh đạo 360 độ, John C. Maxwell chứng minh rằng quyền lực trong hầu haết các tổ chức chủ yếu thuộc về các nhà lãnh đạo cấp trung, những người có một vị thế nhất định, nhưng hiếm khi ý thức được ảnh hưởng và quyền lực của mình. Ông dẫn dắt chúng ta đi từ những ngộ nhận và thách thức phải vượt qua để đến với các nguyên tắc cốt tử của thuật lãnh đạo."),
            BookModel(null, "Tư Duy Nhanh và Chậm","https://th.bing.com/th/id/OIP.Pk4YZLjly5MDfIRC2I4fsAHaLD?w=115&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Daniel Kahneman","Thế Giới","512 trang","Sách",80.000,"Trong tác phẩm Tư Duy Nhanh và Chậm, tác giả đưa ra và phân tích lý giải hai hệ thống tư duy tác động đến sự nhận thức của của con người là Hệ thống 1 gọi là cơ chế nghĩ nhanh, tự động, thường xuyên được sử dụng, cảm tính, rập khuôn và tiềm thức; Hệ thống 2 gọi là cơ chế nghĩ chậm, đòi hỏi ít nỗ lực, ít được sử dụng, dùng logic có tính toán và ý thức."),
            BookModel(null, "Tâm Lý Học Tình Yêu","https://th.bing.com/th/id/OIP.X77Zz-gAq1TTWDTYUFZ26wAAAA?w=130&h=195&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Robert Sternberg","Thanh Niên","400 trang","Sách",100.000,"Cuốn sách Tâm Lý Học Tình Yêu của Robert Sternberg nói về những khía cạnh tâm lý học liên quan đến mối quan hệ tình cảm và tình yêu. Tác giả Robert Sternberg, một nhà tâm lý học nổi tiếng, đưa ra một mô hình tâm lý học về tình yêu gọi là Triangular Theory of Love (Lý thuyết tam giác về tình yêu)."),
            BookModel(null, "Ngoài Hành Tinh","https://th.bing.com/th/id/OIP.S0yBMkiRbF8FaReSWHBmYgAAAA?w=115&h=181&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "David O. Russell – Andrew Auseon","Kim Đồng","350 trang","Sách",90.000,"Đôi bạn thân Gene Brennick và Vince Haskell thường xuyên tiếp xúc với những người ngoài hành tinh kỳ dị như Ông Rêu, Cô Gái Calamari, Bánh Vụn…để viết bài cho Quả Địa Cầu, một bản tin nội bộ chuyên đăng tải những câu chuyện kể về người ngoài hành tinh đang sống lặng lẽ giữa những con người bình thường ở Santa Rosa, California. Bạn bè của Gene và Vince chẳng mấy tin tưởng vào những bài báo này. Gene quyết tâm tìm ra cách nào đó để mãi mãi chấm dứt thân phận của một học sinh lúc nào cũng bị bạn bè tẩy chay, cười nhạo, và khi khám phá ra bí mật sâu kín, tối tăm của một giáo viên địa phương thì Gene tin chắc rằng cậu đã tìm ra được giải pháp. Tuy nhiên, sau khi đã dấn một bước quá xa, Gene và Vince bất thình lình lọt ngay vào trung tâm cuộc xung đột giữa các thiên hà, một cuộc xung đột có thể đặt dấu chấm hết cho tình bạn thân thiết của cả hai, hoặc cho chính sự sống còn của bản thân mỗi người – tùy theo tình huống nào xảy đến trước."),


            BookModel(null, "Cuộc Đời Và Vũ Trụ","https://th.bing.com/th/id/OIP.6Eyfajg2o06CG92m8TYVQgHaKX?w=115&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Einstein","Thế Giới","216 trang","Sách",100.000,"Cuốn sách khai thác và công bố những tư liệu mới nhất về Einstein, làm rõ những giai đoạn, sự kiện và vấn đề trong cuộc sống cá nhân của Einstein.\n" +
                        "\n" +
                        "Cuốn sách cũng chỉ ra và làm rõ những chặng trên con đường khoa học của Einstein, cho thấy những suy tư và trăn trở của ông để đưa ra những lý thuyết vật lý làm thay đổi toàn bộ nền vật lý thế kỷ XX, cũng như cuộc tranh luận của ông với các nhà cơ học lượng tử.\n" +
                        "\n" +
                        "Tác giả đã lột tả được cá tính, tư tưởng chính trị và những đặc điểm trong trí tuệ, nhân cách của Einstein một cách sinh động."),
            BookModel(null, "Vũ Trụ Từ Hư Không","https://th.bing.com/th/id/OIP.TI00VBzfsAQV4JPh5bQ0bwHaHa?w=194&h=194&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Lawrence M. Krauss","Thế Giới"," 300 trang","Sách",120.000,"Suốt hơn hai nghìn năm qua, câu hỏi, tại sao tồn tại một cái gì đó thay vì không có gì? đã luôn hiện diện như một thách thức đối với quan niệm cho rằng vũ trụ của chúng ta - nơi chứa đựng tổ hợp bao la giữa các vì sao, các thiên hà, con người và ai biết còn cái gì nữa - có thể đã sinh ra mà không có một thiết kế, một ý đồ hay mục đích nào. Dù rằng câu hỏi này thường được đóng khung hạn chế như là một câu hỏi triết học hay tôn giáo, thì trước hết và quan trọng nhất đây chính là câu hỏi về tự nhiên vì thế nơi phù hợp để thử thách và giải quyết câu hỏi này, đầu tiên và quan trọng nhất chính là khoa học."),
            BookModel(null, "Sống Một Cuộc Đời Đáng Sống","https://th.bing.com/th/id/OIP.ymelKxmyyHbzYjURNnFNFwHaLm?w=119&h=187&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Maria Shriver","Trẻ","224 trang","Sách",100.000,"Cuốn sách này là một tác phẩm cá nhân của tác giả, Maria Shriver, nơi cô chia sẻ về cuộc hành trình của mình trong việc tìm kiếm ý nghĩa cuộc sống và cách sống một cuộc đời mang giá trị. Maria Shriver là một phụ nữ nổi tiếng, là người viết, nhà báo và hoạt động xã hội.\n" +
                        "\n" +
                        "Trong cuốn sách, Maria tập trung vào các giá trị nhân văn và đạo đức, với mục tiêu khuyến khích độc giả suy nghĩ sâu hơn về ý nghĩa của cuộc sống và cách chúng ta có thể làm cho cuộc sống của mình trở nên có ý nghĩa hơn. Cô chia sẻ những câu chuyện cá nhân, trải nghiệm, và bài học mà cô đã học qua cuộc hành trình của mình.\n" +
                        "Sống Một Cuộc Đời Đáng Sống là một cuốn sách đầy cảm hứng và thúc đẩy độc giả suy ngẫm về cách tạo ra ý nghĩa và giá trị trong cuộc sống cá nhân và trong xã hội. Cuốn sách này cung cấp sự lãnh đạo tinh thần và khái niệm về việc sống một cuộc đời có ý nghĩa và đáng sống.") ,
            BookModel(null, "Cuộc Sống Không Giới Hạn","https://th.bing.com/th/id/OIP.i7i9vrXHEUBf84KofmGUmwHaKw?w=120&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Nick Vujicic","Tổng hợp Thành Phố Hồ Chí Minh"," 288 trang","Sách",90.000,"Cuộc Sống Không Giới Hạn của Nick Vujicic kể về cuộc hành trình của một người không có cánh tay và chân, nhưng đã vượt qua nhiều khó khăn để sống cuộc đời đầy ý nghĩa và cảm hứng. Cuốn sách này nói về sự quyết tâm và niềm tin của Nick, khuyến khích độc giả đối mặt với khó khăn bằng niềm tin và hành động. Nick Vujicic truyền cảm hứng và chia sẻ những bài học quý báu về tinh thần và sức mạnh của niềm tin vào bản thân.\n" +
                        "Cuộc Sống Không Giới Hạn của Nick Vujicic kể về cuộc hành trình của một người không có cánh tay và chân, nhưng đã vượt qua nhiều khó khăn để sống cuộc đời đầy ý nghĩa và cảm hứng. Cuốn sách này nói về sự quyết tâm và niềm tin của Nick, khuyến khích độc giả đối mặt với khó khăn bằng niềm tin và hành động. Nick Vujicic truyền cảm hứng và chia sẻ những bài học quý báu về tinh thần và sức mạnh của niềm tin vào bản thân.") ,
            BookModel(null, "Sống Hết Mình Cho Ngày Hôm Nay","https://th.bing.com/th/id/OIP.l2n-GaWG5NEhKYgPs5gk1wAAAA?w=115&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Taketoshi Ozawa","Thế Giới","300 trang","Sách",100.000,"Taketoshi Ozawa khuyến khích độc giả tập trung vào thời gian hiện tại, chứ không phải lãng phí thời gian lo lắng về quá khứ hoặc lo sợ về tương lai. Cuốn sách nói về cách tạo ra một tư duy tích cực, thực hiện mục tiêu cá nhân và làm cho mọi ngày trở nên đáng nhớ và đáng sống.\n" +
                        "\n" +
                        "Ngoài ra, cuốn sách cũng thảo luận về việc phát triển sự thấu hiểu về ý nghĩa cuộc sống và tạo ra những thay đổi tích cực trong cách chúng ta sống. Cuốn sách này thường đề cập đến các chủ đề như tâm lý tích cực, quản lý thời gian, và việc xây dựng một cuộc sống có mục tiêu.") ,
            BookModel(null, "Đừng Lựa Chọn An Nhàn Khi Còn Trẻ","https://th.bing.com/th/id/OIP.FGnkB6NokTRwGQB5lljtEAHaHa?w=177&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Cảnh Thiên","Thế Giới","320 trang","Sách",90.000,"Câu nói Đừng Lựa Chọn An Nhàn Khi Còn Trẻ – Cảnh Thiên thường ám chỉ rằng trong tuổi trẻ, bạn không nên chấp nhận sự an nhàn và thoải mái quá mức, mà thay vào đó nên tận dụng thời gian và năng lượng của mình để đối mặt với thách thức, khám phá cuộc sống, và phấn đấu đạt được mục tiêu cá nhân.\n" +
                        "\n" +
                        "Tác giả Cảnh Thiên có thể thể hiện sự quý trọng của sự trẻ trung và nhiệt huyết trong việc tạo ra những trải nghiệm đáng nhớ và đạt được những thành tựu đáng kể trong cuộc sống. Ý nghĩa của câu này là khuyến khích người trẻ không ngại khó khăn, mạo hiểm, và khám phá thế giới xung quanh họ để phát triển tốt hơn và thực hiện đam mê của mình."),
            BookModel(null, "Đừng Lựa Chọn An Nhàn Khi Còn Trẻ","https://th.bing.com/th/id/OIP.FGnkB6NokTRwGQB5lljtEAHaHa?w=177&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Cảnh Thiên","Thế Giới","320 trang","Sách",90.000,"Câu nói Đừng Lựa Chọn An Nhàn Khi Còn Trẻ – Cảnh Thiên thường ám chỉ rằng trong tuổi trẻ, bạn không nên chấp nhận sự an nhàn và thoải mái quá mức, mà thay vào đó nên tận dụng thời gian và năng lượng của mình để đối mặt với thách thức, khám phá cuộc sống, và phấn đấu đạt được mục tiêu cá nhân.\n" +
                        "\n" +
                        "Tác giả Cảnh Thiên có thể thể hiện sự quý trọng của sự trẻ trung và nhiệt huyết trong việc tạo ra những trải nghiệm đáng nhớ và đạt được những thành tựu đáng kể trong cuộc sống. Ý nghĩa của câu này là khuyến khích người trẻ không ngại khó khăn, mạo hiểm, và khám phá thế giới xung quanh họ để phát triển tốt hơn và thực hiện đam mê của mình."),
            //-----------------
            BookModel(null, "Nobita Và Truyền Thuyết Vua Mặt Trời","https://th.bing.com/th/id/OIP.9rA6Kx8lLMGoWYy5JAbqkgHaHa?w=208&h=208&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Fujiko F. Fujio","Kim Đồng","188 trang","Truyện Tranh",120.000,"Tập truyện Nobita Và Truyền Thuyết Vua Mặt Trời trong Doraemon kể về cuộc phiêu lưu của Nobita và bạn bè đến một hòa bình đảo xa xôi. Trên đảo, họ phát hiện một viên đá quý đặc biệt có khả năng biến đổi cuộc sống. Cuộc tìm kiếm viên đá này dẫn họ đến khám phá truyền thuyết về Vua Mặt Trời." +
                        "\n" +
                        "Tác giả Cảnh Thiên có thể thể hiện sự quý trọng của sự trẻ trung và nhiệt huyết trong việc tạo ra những trải nghiệm đáng nhớ và đạt được những thành tựu đáng kể trong cuộc sống. Ý nghĩa của câu này là khuyến khích người trẻ không ngại khó khăn, mạo hiểm, và khám phá thế giới xung quanh họ để phát triển tốt hơn và thực hiện đam mê của mình."),
            BookModel(null, "Rồng Thần Xuất Hiện","https://th.bing.com/th/id/OIP.N_VDULRGnkNjvR7MolKFuAHaJl?pid=ImgDet&w=182&h=234&c=7&dpr=1.3",
                "Akira Toriyama. Akira Toriyama","Shueisha","188 trang","Truyện Tranh",90.000,"Trong tập truyện này, Goku và nhóm của mình đang tìm kiếm viên Ngọc Rồng cuối cùng để có thể triệu hồi Rồng Thần Shenron. Họ phải đối mặt với một tên trùm ác đang âm mưu sử dụng viên đá cuối cùng để thực hiện điều ước độc ác. Cuộc phiêu lưu đầy kịch tính này bao gồm trận chiến gay cấn và thông điệp về tình bạn và sức mạnh."),
            BookModel(null, "Công Đức Xây Chùa","https://th.bing.com/th/id/OIP.CszXwc9w_ZkRImkR55K7lgAAAA?w=199&h=314&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Nam Cao","Kim Đồng","97 trang","Truyện Tranh",50.000,"Truyện kể về cuộc đời của Trạng Quỳnh, một người nông dân tốt bụng và hiếu thảo. Trạng Quỳnh dành cả đời mình để tích góp tiền xây dựng một ngôi chùa trong lòng làng để cầu nguyện cho gia đình và làng lành của mình. Truyện nhấn mạnh vào tinh thần hiếu thảo và lòng nhân ái của nhân vật chính. Cuối cùng, sau khi chết, Trạng Quỳnh được xem là một người có công đức lớn và được người dân thờ phụng như một vị thánh."),
            BookModel(null, "Nobita - Vũ Trụ Phiêu Lưu Kí","https://th.bing.com/th/id/OIP.zsNvBwg0YOGzahJPyOGargHaLi?w=200&h=312&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Fujiko F. Fujio","Kim Đồng","128 trang","Truyện Tranh",120.000,"Trong tập truyện Nobita - Vũ Trụ Phiêu Lưu Kí, Nobita và bạn bè của mình sử dụng một chiếc kính thiên văn để khám phá vũ trụ. Họ bắt đầu một cuộc hành trình kỳ diệu, gặp gỡ nhiều loài người ngoài hành tinh và trải qua nhiều cuộc phiêu lưu thú vị. Tập truyện này tập trung vào sự hiếu kỳ và sự tò mò của Nobita và bạn bè, và nhấn mạnh tình bạn và hòa bình giữa các loài trong vũ trụ."),
            BookModel(null, "Chú Mèo Máy Đến Từ Tương Lai","https://th.bing.com/th/id/OIP.NFjiHIVjKuDOveentMYhAgHaLr?w=199&h=313&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Fujiko F. Fujio","Kim Đồng","192 trang","Truyện Tranh",120.000,"Chú Mèo Máy Đến Từ Tương Lai là một tập truyện trong loạt Doraemon. Tập này kể về Doraemon, một chú mèo máy đến từ tương lai, được gửi về để giúp đỡ cậu bé Nobita. Doraemon đem theo một túi đồ hi-tech đầy công cụ và thiết bị tương lai để giúp Nobita giải quyết các vấn đề trong cuộc sống hàng ngày của mình."),
            BookModel(null, "Vị Hôn Thê Đến Từ Tương Lai","https://th.bing.com/th/id/OIP.R7tD9nmDoCE15Br6OHHZxAHaKC?w=147&h=200&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Hiroshi Fujimoto và Fujiko F. Fujio","Kim Đồng","164 trang","Truyện Tranh",90.000,"Tập truyện Vị Hôn Thê Đến Từ Tương Lai trong loạt Doraemon kể về cuộc gặp gỡ giữa Shin, cậu bé bút chì (Shin-chan), và Shizuka, một cô bé xinh đẹp đến từ tương lai. Trong tập này, Shizuka được gửi từ tương lai để cứu Shin và bạn bè của mình khỏi một tình huống nguy hiểm."),
            BookModel(null, "Ẩm Thực Đường Phố","https://th.bing.com/th/id/OIP.bhQdZRvxUSW9RwjEowEHogHaKC?pid=ImgDet&w=182&h=246&c=7&dpr=1.3",
                "Hiroshi Fujimoto và Fujiko F. Fujio","Kim Đồng","164 trang","Truyện Tranh",90.000,"Tập truyện Ẩm Thực Đường Phố trong loạt Doraemon kể về Shin, cậu bé bút chì (Shin-chan), và cuộc phiêu lưu ẩm thực của anh ấy. Trong tập này, Shin-chan và bạn bè của mình thực hiện một cuộc hành trình thú vị qua đường phố để khám phá và thử nghiệm các món ăn đường phố ngon lành. Cuộc hành trình này đưa ra một cái nhìn về văn hóa ẩm thực đa dạng và tạo ra nhiều tình huống hài hước và lý thú cho nhóm bạn."),
            BookModel(null, "Cuộc Phiêu Lưu Của Pippi","https://th.bing.com/th/id/OIP.3qPAFEg9NlMZfpjTtjkPCgAAAA?w=119&h=185&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "Kosaku Anakubo","Kim Đồng","196 trang","Truyện Tranh",120.000,"Pokémon - Cuộc phiêu lưu của Pippi là một trong những Series về Pokémon đình đám tại Việt Nam. Cách đây gần 2 thập kỉ, cùng với Pokémon Đặc biệt, Pippi đã trở thành một trong những tác phẩm gây bão vì mức độ hài hước, lầy lội và siêu vui nhộn tới từ chú Pippi ham ăn ham ngủ.\n" +
                        "\n" +
                        "Với Pippi, ở đâu có chiến đấu, ở đó còn hi vọng!! Chính sự phấn đấu không ngừng của chú Pokémon này bên cạnh các Pokémon quen thuộc khác như Pikachu, Togepy, Fushigidane... đã đem lại những phút giây thư giãn thực sự với ngay cả những độc giả chưa biết quá nhiều về thế giới Pokémon.")
//            BookModel(null, "","",
//                "","Truyện Tranh",120.000,""),

        )

        for (book in books) {
            // Kiểm tra xem cuốn sách đã tồn tại trong cơ sở dữ liệu hay chưa
            val query = myRef.orderByChild("btitle").equalTo(book.btitle)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                    } else {
                        // Cuốn sách chưa tồn tại, thêm nó vào cơ sở dữ liệu
                        val newUser = myRef.push()
                        val userId = newUser.key
                        book.bid = userId // Đặt ID được tạo bởi push() trở lại cho đối tượng BookModel
                        newUser.setValue(book)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Xử lý lỗi nếu cần
                }
            })
        }
    }
}


