package com.qunar.qtalk.cricle.camel.service;

import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.event.CommentEventMsgContentModel;
import com.qunar.qtalk.cricle.camel.common.event.EventModel;
import com.qunar.qtalk.cricle.camel.common.util.DozerUtils;
import com.qunar.qtalk.cricle.camel.entity.CamelMessage;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * CamelCommentServiceTest
 *
 * @author binz.zhang
 * @date 2019/2/13
 */
public class CamelCommentServiceTest extends BaseTest {
    @Resource
    private CamelCommentService camelCommentService;

    @Resource
    private DozerUtils dozerUtils;

    @Test
    public void isDelete() {
        System.out.println(camelCommentService.isExistComment("******"));
    }



    @Test
    public void updateSql(){
        camelCommentService.updateStatus();
    }

//    @Test
//    public void updateParentSql(){
//        camelCommentService.updateSuperParentUUID();
//    }
//
    @Test
    public void pull(){
//        String potUUID = "0-F62DE459306B4B2A884B54C7EAAF0CB5";
//        List<String> hot = camelCommentService.getHotCommentUUID(potUUID,3);
//        Set<String> hotSet = new HashSet<>(hot);
//        List<CamelComment>comment1 = camelCommentService.getTheComment(900,50,potUUID,true);
//        List<CamelComment> hotIncomment1 = comment1.stream().filter(x->hotSet.contains(x.getCommentUUID())).collect(Collectors.toList());
//        List<CamelComment>comment2 = camelCommentService.getTheComment(900,50,potUUID,false);
//        List<CamelComment> hotIncomment2 = comment2.stream().filter(x->hotSet.contains(x.getCommentUUID())).collect(Collectors.toList());
//
//        System.out.println("ok");

        EventModel es = new EventModel();
        CommentEventMsgContentModel msg = new CommentEventMsgContentModel();
        msg.setContent("ok");
        msg.setReadState(0);
        msg.setEventType(1);
        msg.setToAnonymousPhoto("a photo ");
        msg.setUserToHost("sa");
       // es.setContent(msg);
        msg.setUuid("asdjsa");
        CamelMessage MD = dozerUtils.map(es,CamelMessage.class);
        System.out.println("ok");






    }
}
