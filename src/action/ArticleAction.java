﻿package action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import service.ArticleService;
import service.CommentService;
import model.Article;
import model.Comment;
import model.User;

import dao.BaseDAO;

import model.Article;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@Controller(value="ArticleAction")  @Scope("prototype")
public class ArticleAction extends ActionSupport implements ModelDriven<Article>{
	
	Article article=new Article();
	List<Article> articles;
	
	@Resource(name="ArticleService")
	ArticleService a_service;
	
	@Resource(name="CommentService")
	CommentService c_service;
	
    String keyword="";
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public Article getModel() {
		return article;
	}
	
	public ArticleService getA_service() {
		return a_service;
	}

	public void setA_service(ArticleService aService) {
		a_service = aService;
	}

	//新发布一篇文章(这里强制设置状态为1)
	public String saveArticle() {
		
		/*设置文章的所有者*/
		User author=(User) ServletActionContext.getRequest().getSession().getAttribute("user");
		getModel().setAuthor(author.getUsername());
		System.out.println(author.getUsername());
		
		/*更改文章状态,1表示已发布*/
		getModel().setState(1);
		System.out.println(getModel().getTitle()+getModel().getAcontent());
		if(a_service.saveArticle(getModel()))
		{
			this.addActionMessage("发布成功！");	
		}		
		return INPUT;	
	}
	
//	//浏览所有已发布文章
//	public String browseValidArticles()
//	{
//		articles=a_service.listAllValidArticles();
//		ServletActionContext.getRequest().getSession().setAttribute("articles",articles);
//		return "resultList";
//	}
	
    //按页加载验证过的文章
	public String browseArticlesByPage(){
		int page=Integer.valueOf(ServletActionContext.getRequest().getParameter("page"));
		int pagesize=5;
		System.out.println(page+" "+pagesize);
		articles=a_service.listValidArticlesByPage(page, pagesize);
		ServletActionContext.getRequest().getSession().setAttribute("articles",articles);
		return "resultList";
	}
	
	//根据标题查询文章
	public String searchArticles() {
	    String keyword=this.getKeyword();
		String hql="from Article as a where a.title like '%"+keyword+"%'  or a.acontent like '%"+keyword+"%'  and a.state=1";
		System.out.println(this.getKeyword());
		System.out.println(hql);
		try{	
			articles=a_service.searchArticleByKeyword(hql);
			ServletActionContext.getRequest().getSession().setAttribute("articles",articles);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "resultList";	
	}
	
	//根据id查询文章
	public String loadArticle() {
		
		int id=Integer.valueOf(ServletActionContext.getRequest().getParameter("id"));
		Article tempArticle=a_service.loadArticle(id);
		ServletActionContext.getRequest().getSession().setAttribute("tempArticle",tempArticle);
		
		//根据id加载这篇文章的评论
		List<Comment> comments=c_service.listAllValidComments(tempArticle.getArticleId());
		//将结果comments存入session
		ServletActionContext.getRequest().getSession().setAttribute("comments", comments);
		return "result";	
	}

	
}
