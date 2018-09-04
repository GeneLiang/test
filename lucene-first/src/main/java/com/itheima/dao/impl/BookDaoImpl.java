package com.itheima.dao.impl;

import com.itheima.dao.BookDao;
import com.itheima.po.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {
    /**
     * 查询全部图书列表
     */
    public List<Book> findAllBooks() {
        //创建结果集集合
        List<Book> bookList = new ArrayList<Book>();

        Connection connection = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;

        try {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            //创建数据库连接对象
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/83_lucene", "root","root");
            //定义sql语句
            String sql = "select * from book";
            //创建prepareStatement对象
            psmt = connection.prepareStatement(sql);
            //设置参数

            //执行
            rs = psmt.executeQuery();
            //处理结果集
            while (rs.next()){
                //创建图书对象
                Book book = new Book();

                book.setId(rs.getInt("id"));
                book.setBookname(rs.getString("bookname"));
                book.setPrice(rs.getFloat("price"));
                book.setPic(rs.getString("pic"));
                book.setBookdesc(rs.getString("bookdesc"));

                bookList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //释放资源
                if(rs!=null)    rs.close();
                if(psmt!=null) psmt.close();
                if (connection!=null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return bookList;
    }
}
