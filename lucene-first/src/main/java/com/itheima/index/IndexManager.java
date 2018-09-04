package com.itheima.index;

import com.itheima.dao.BookDao;
import com.itheima.dao.impl.BookDaoImpl;
import com.itheima.po.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 索引管理类
 */
public class IndexManager {

    //定义索引库的位置常量
    public final static  String INDEX_PATH = "D:\\test\\lucene\\";

    /**
     * 索引流程实现
     */
    @Test
    public void createIndex() throws Exception {
        //1.采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.findAllBooks();

        //2.建立文档对象（Document）
        List<Document> docList = new ArrayList<Document>();
        for (Book book : bookList) {
            //创建文档对象
            Document document = new Document();
            /**
             * 给文档对象添加域
             */
            document.add(new TextField("bookId",book.getId()+"", Field.Store.YES));
            document.add(new TextField("bookName",book.getBookname(),Field.Store.YES));
            document.add(new TextField("price",book.getPrice()+"", Field.Store.YES));
            document.add(new TextField("pic",book.getPic(), Field.Store.YES));
            document.add(new TextField("bookDesc",book.getBookdesc(), Field.Store.YES));

            docList.add(document);
        }
        //3.建立分析器（分词器）对象（Analyzer）
//        Analyzer analyzer = new StandardAnalyzer();
        /**
         * 使用ik中文分词器
         */
        Analyzer analyzer = new IKAnalyzer();

        //4.建立索引库配置对象（IndexWriterConfig），配置索引库
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);

        //5.建立索引库目录对象（Directory），指定索引库的位置
        File file = new File(INDEX_PATH);
        Directory directory = FSDirectory.open(file);

        //6.建立索引库操作对象（IndexWriter），操作索引库
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);

        //7.使用IndexWriter，把文档对象写入索引库
        for (Document document : docList) {
            /**
             * addDocument方法，把文档对象写入索引库
             */
            indexWriter.addDocument(document);
        }

        //8.释放资源
        indexWriter.close();
    }

    @Test
    public void readIndex() throws Exception {
        //1.建立分析器对象（Analyzer），用于分词
//        Analyzer analyzer = new StandardAnalyzer();
        /**
         * 使用ik中文分词器
         */
        Analyzer analyzer = new IKAnalyzer();

        //2.建立查询对象（Query）
        //2.1建立查询解析器对象
        QueryParser parser = new QueryParser("bookName", analyzer);
        //2.2使用查询器解析对象，解析表达式，实例化Query对象
        Query query = parser.parse("bookName:java");

        //3.建立索引库目录对象（Directory），指定索引库的位置
        Directory directory = FSDirectory.open(new File(INDEX_PATH));

        //4.建立索引读取对象（IndexReader），把索引数据读取到内存中
        IndexReader reader = DirectoryReader.open(directory);

        //5.建立索引搜索对象（IndexSearcher），执行搜索，返回搜索的结果集（TopDocs）
        IndexSearcher searcher = new IndexSearcher(reader);

        /**
         * 执行搜索的方法：search
         */
        TopDocs topDocs = searcher.search(query, 10);

        //6.处理结果集
        System.out.println("实际搜索到的结果数量："+topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc:scoreDocs){
            System.out.println("=========================");
            //取出文档id和分值
            int docId = scoreDoc.doc;
            float score = scoreDoc.score;
            System.out.println("当前的文档id："+docId+"，当前的文档分值："+score);

            //根据文档id获取文档数据
            Document document = searcher.doc(docId);
            System.out.println("图书id："+document.get("bookId"));
            System.out.println("图书名称："+document.get("bookName"));
            System.out.println("图书价格："+document.get("price"));
            System.out.println("图书图片："+document.get("pic"));
            System.out.println("图书图片描述："+document.get("bookDesc"));

        }

        //7.释放资源
        reader.close();
    }
}
