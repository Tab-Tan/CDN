package cn.awall.awalladmin;

import cn.awall.awalladmin.service.impl.HotServiceImpl;
import cn.awall.awalladmin.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
class AwallAdminApplicationTests {

//    @Autowired
//    ArticleMapper articleMapper;
//
//    @Autowired
//    CommentMapper commentMapper;
//
//    @Autowired
//    GroupMapper groupDao;
//
//    @Autowired
//    NoticeMapper noticeMapper;
//
//    @Autowired
//    ProtectMapper protectMapper;
//
//    @Autowired
//    SubMapper subMapper;
//    @Autowired
//    UserMapper userMapper;
//
//    /**
//     * 生成含有随机盐的密码
//     */
//    public static String generate(String password) {
//        Random r = new Random();
//        StringBuilder sb = new StringBuilder(16);
//        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
//        int len = sb.length();
//        if (len < 16) {
//            for (int i = 0; i < 16 - len; i++) {
//                sb.append("0");
//            }
//        }
//        String salt = sb.toString();
//        password = md5Hex(password + salt);
//        System.out.println("password = " + password);
//        char[] cs = new char[48];
//        for (int i = 0; i < 48; i += 3) {
//            cs[i] = password.charAt(i / 3 * 2);
//            char c = salt.charAt(i / 3);
//            cs[i + 1] = c;
//            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
//        }
//        return new String(cs);
//    }
//
//    /**
//     * 校验密码是否正确
//     */
//    public static boolean verify(String password, String md5) {
//        char[] cs1 = new char[32];
//        char[] cs2 = new char[16];
//        for (int i = 0; i < 48; i += 3) {
//            cs1[i / 3 * 2] = md5.charAt(i);
//            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
//            cs2[i / 3] = md5.charAt(i + 1);
//        }
//        String salt = new String(cs2);
//        return md5Hex(password + salt).equals(new String(cs1));
//    }
//
//    /**
//     * 获取十六进制字符串形式的MD5摘要
//     */
//    public static String md5Hex(String src) {
//        try {
//            MessageDigest md5 = MessageDigest.getInstance("MD5");
//            byte[] bs = md5.digest(src.getBytes());
//            return new String(new Hex().encode(bs));
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    @Test
//    void contextLoadsArticle() {
//
//        Article article = new Article(null, 502006690l, null, "内容", "话题", "url", "分类", 123, 456l);
//
//
////        int i = articleDao.addArticle(article);
//
////        int i = articleDao.deleteArticleById(1L);
//
////        List<Article> articles = articleDao.selectArticleByArticle(article1);
////        System.out.println("articles = " + articles);
//
////        System.out.println("i = " + i);
//
//        articleMapper.addArticle(article);
//
//
//    }
//
//    @Test
//    void contextLoadsComment() {
//
////        Comment comment = new Comment();
////        comment.setComId(1L);
////        comment.setUserId(122L);
////        comment.setArticleId(12222L);
////        comment.setContent("评论内容！");
////        comment.setDate(null);
////        comment.setToId(122L);
////        int i = commentDao.addComment(comment);
////        int i = commentDao.deleteCommentById(2L);
//
////        int i = commentDao.updateCommentById(comment);
////        System.out.println("i = " + i);
//
////        List<Comment> comments = commentDao.selectCommentByComment(comment);
////        System.out.println("comments = " + comments);
//
//        Comment comment = new Comment();
//        comment.setComId(3L);
//        List<Comment> comments = commentMapper.selectCommentByComment(comment);
//        System.out.println("comments = " + comments);
//
//    }
//
//    @Test
//    void contextLoadsGroup() {
//
//
/////*        Group group = new Group(1234L, 234L, 456L);
////        int i = groupDao.addGroup(group);
////        */
////        int i = groupDao.deleteGroupByTd(1234L);
//
//        Group group = new Group(null, 234111L, 451116L);
////        List<Group> groups = groupDao.selectGroup(group);
//////        System.out.println("i = " + i);
////
////        System.out.println("groups = " + groups);
////        for (int i = 0; i < groups.size(); i++) {
////            System.out.println("groups = " + groups.get(i));
////        }
//        groupDao.addGroup(group);
//    }
//
//    @Test
//    void contextLoadsNotice() {
//
//        Notice notice = new Notice(null, null, null, null, 5671L);
//        noticeMapper.addNotice(notice);
//
////        noticeDao.deleteNoticeById(124L);
//
////        noticeDao.updateNotice(notice);
//
////        List<Notice> notices = noticeDao.selectNotice(notice);
////        System.out.println("notices = " + notices);
//
//    }
//
//    @Test
//    void contextLoadsProtect() {
//
//
//        Protect protect = new Protect(null, "这是标题5", "这是状态哦5", 2225L);
////        protectDao.addProtect(protect);
//
////        protectDao.deleteProtectById(124L);
//
////        protectDao.updateProtect(protect);
//
//        List<Protect> protects = protectMapper.selectProtect(protect);
//        System.out.println("protects = " + protects);
//    }
//
//    @Test
//    void contextLoadsSub() {
//
//        Sub sub = new Sub(null, 123456L, 231321L);
//
//        subMapper.addSub(sub);
//
////        subDao.deleteSub(7L);
//
////        subDao.updateSub(sub);
//
////        List<Sub> subs = subDao.selectSub(sub);
////        System.out.println("subs = " + subs);
//
//
//    }
//
//    @Test
//    void test01() throws NoSuchAlgorithmException {
//
//        // 加密+加盐
//        String password1 = generate("admin");
//        System.out.println("结果：" + password1 + "   长度：" + password1.length());
//        // 解码
//        System.out.println(verify("admin1", password1));
//        // 加密+加盐
//        String password2 = generate("admin");
//        System.out.println("结果：" + password2 + "   长度：" + password2.length());
//        // 解码
//        System.out.println(verify("admin", password2));
//
//    }
//
//    @Test
//    public void test02() {
//
//        //前端传来的手机号
//        String tel = "17634509867";
//
//        //前端传来密码
//        String password = "admin";
//
//        //为该用户生成一个随机的盐
//        String salt = UUID.randomUUID().toString().substring(0, 6);
//
//        System.out.println("salt = " + salt);
//
//        System.out.println("开始加密...");
//        //将盐加密
//        String salts = DESUtils.encrypt(salt);
//        System.out.println("加密后: salts = " + salts);
//
//        //使用加密后的盐和密码拼接后md5加密
//        String enPassword = Md5Utils.encoder(password, salts);
//
//        User user = new User();
//        user.setTel(tel);
//        user.setPassword(enPassword);
//        user.setSalt(salt);
//
//
//        //将加密后的密码和未加密的盐存到数据库
//        int i = userMapper.addUser(user);
//        System.out.println("i = " + i);
//
//        System.out.println("开始解密...");
//
//        String saltj = DESUtils.decrypt(salts);
//        System.out.println("解密后: saltj = " + saltj);
//
//
//    }

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HotServiceImpl hotService;

    @Test
    public void test05() {


        for (int i = 0; i < 10; i++) {

            hotService.heating("71", "zan");
        }
        for (int i = 0; i < 20; i++) {

            hotService.heating("73", "zan");
        }
        for (int i = 0; i < 8; i++) {

            hotService.heating("74", "zan");
        }

        Map<String, Integer> hots = hotService.getHot();
        System.out.println("hots = " + hots);

        for (Map.Entry<String, Integer> hot : hots.entrySet()) {
            System.out.println(hot.getKey());
            System.out.println(hot.getValue());
        }


    }


}
