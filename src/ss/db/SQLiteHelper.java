package ss.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections.map.StaticBucketMap;

import com.alibaba.fastjson.parser.deserializer.SqlDateDeserializer;

import ss.object.Article;

public class SQLiteHelper {

	public static SQLiteHelper mySqLiteHelper = new SQLiteHelper();

	Connection conn;

	public Connection getConn() {
		return conn;
	}

	PreparedStatement prep;
	int count = 0;

	private SQLiteHelper() {
		try {
			Init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Init() throws ClassNotFoundException, SQLException {
		Class class1 = Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:article.sqlite");
		Statement stat = conn.createStatement();
		ResultSet rsTables = conn.getMetaData().getTables(null, null, "paper", null);
		if (rsTables.next()) {
			System.out.println("article表存在");
		} else {
			// stat.executeUpdate("drop table if exists paper;");
			stat.executeUpdate("create table paper (username, article, content, tags, url);");
		}

		prep = conn.prepareStatement("insert into paper values (?, ?, ?, ?, ?);");
	}

	public void Insert(Article article) {
		try {
			prep.setString(1, article.getUser_name());
			prep.setString(2, article.getArticle_name());
			prep.setString(3, article.getContent());
			prep.setString(4, article.getTags());
			prep.setString(5, article.getArticle_url());
			prep.addBatch();
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
