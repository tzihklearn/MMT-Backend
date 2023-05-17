package com.sipc.mmtbackend.utils.lark;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 飞书消息卡片实体类
 * @author tzih
 * @version 1.0
 * @since 2023/4/10 15:26
 */
@Data
public class Entry {

    /**
     * 消息类型
     */
    private String msg_type;

    /**
     * 消息卡片
     */
    private Card card;

    /**
     * 空参构造器方法
     */
    public Entry() {

    }

    /**
     * 消息卡片
     */
    @Data
    public static class Card {

        /**
         * 卡片元素列表
         */
        private List<Element> elements;

        /**
         * 消息卡片标题
         */
        private Header header;

        /**
         * 卡片元素实体类
         */
        @Data
        public static class Element {
            private String tag;
            private String content;
            private Img img;
            private String mode;
            private boolean preview;
            private String src;

            @Data
            public static class Img {
                private String img_key;
                private Title alt;
            }
        }

        /**
         * 卡片标题实体类
         */
        @Data
        public static class Header {

            /**
             * 卡片标题内容实体类
             */
            private Title title;

            /**
             * 标题主题颜色
             */
            private String template;
        }
    }

    @Data
    public static class Title {
        /**
         * 卡片标题内容
         */
        private String content;

        /**
         * 固定值plain_text。
         */
        private String tag;
//        private String template;
    }

    public Entry(String title, String url, String requestMessage, String content) {

        //设置card
        Card card = new Card();
        //设置header
        Card.Header header = new Card.Header();
        Title headerTitle = new Title();
        headerTitle.setTag("plain_text");
        headerTitle.setContent(title + "请求异常");

        //组装header
//        header.setTemplate("wathet");
        header.setTemplate("blue");
        header.setTitle(headerTitle);

        card.setHeader(header);

        //组装elements
        List<Card.Element> elements = new ArrayList<>();

        Card.Element element1 = new Card.Element();
        element1.setTag("markdown");
        element1.setContent("<at id=all></at><br>" +"请求路径：**" + url + "**<br>" + "请求详细：" + requestMessage );
        elements.add(element1);

        Card.Element element2 = new Card.Element();
        element2.setTag("hr");
        elements.add(element2);

        Card.Element element3 = new Card.Element();
        element3.setTag("markdown");
        element3.setContent(content);
        elements.add(element3);

        card.setElements(elements);

        this.msg_type = "interactive";
        this.card = card;

    }

}




