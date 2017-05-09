package ss.object;

public class Article {

	private String user_name;
	private String article_name;
	private String article_content;
	private String article_url;
	private String article_tags;
//	public String article_html;
	
	public String getUser_name() {
		return user_name;
	}

	public String getArticle_name() {
		return article_name;
	}

	public String getContent() {
		return article_content;
	}

	public String getArticle_url() {
		return article_url;
	}

	public String getTags() {
		return article_tags;
	}
	
	public Article(String username,String articlename,String content,String cururl,String tags) {
		user_name = username;
		article_name = articlename;
		article_content = content;
		article_url = cururl;
        article_tags = tags;
	}
}
