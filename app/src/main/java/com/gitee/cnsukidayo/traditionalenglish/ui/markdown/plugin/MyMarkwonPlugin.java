package com.gitee.cnsukidayo.traditionalenglish.ui.markdown.plugin;

import android.content.Context;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.commonmark.node.Heading;
import org.commonmark.node.Node;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.core.MarkwonTheme;

/**
 * @author cnsukidayo
 * @date 2023/1/12 12:20
 */
public class MyMarkwonPlugin extends AbstractMarkwonPlugin {

    private Context context;

    private MyMarkwonPlugin(Context context) {
        this.context = context;
    }

    public static MyMarkwonPlugin getInstance(Context context) {
        return new MyMarkwonPlugin(context);
    }

    @Override
    public void configure(@NonNull Registry registry) {
        /*
        一个插件当中还可以配置多个插件,相当于插件继承? 1
        第一个参数代表当前插件需要哪一个插件,第二个参数实际上是一个回调函数,它会把第一个申请到的插件传入第二个参数的apply实现类方法.
        并且如果当前的markwon没有使用第一个参数对应的插件,则这里会报错.
        并且该方法应该会在一开始就调用
        */
        super.configure(registry);
    }

    @Override
    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
        // 3
    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        // 配置解析器功能
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        /* 6
        工厂相当于用来处理每个节点,如果有args0所代表的节点,则使用该节点解析器.注意SpanFactory是用来渲染节点的,而不是用于处理节点功能的.
        addFactory方法已被废弃,实则内部调用prependFactory方法,就是为某个节点添加多个SpanFactory.
        RenderProps是一个集合,存储的内容是某个Node解析完毕之后将其所有的状态存储到RenderProps中,通过RenderProps来获取存储在其中的Node的值.
        因为不同的Node有不同的状态值类型,它们是通过Prop来区分的,而RenderProps是通过Map来获取value的,Map的key就是Prop,也就是通过状态来获取状态值.
        但是真正获取的时候是通过CoreProps先区分类型,然后再将RenderProps传入获取具体的值.
        这个方法就是用来返回工厂的,针对不同的状态返回不同的渲染工厂.
        requireFactory
        getFactory 这两个方法本质上没有区别

        */

//        builder.setFactory(Heading.class, (configuration, props) -> new NoUnderLineHeadingSpan(configuration.theme(), CoreProps.HEADING_LEVEL.get(props)));
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        /* 5
        调用完beforeRender方法后会调用Visitor来访问每个节点
        configureVisitor主要的作用在于,可以对当前节点设置其具体的Spanned对象
        注意一个节点只能配置一个NodeVisitor,我们可以通过NodeVisitor来决定当前的节点使用哪个Spanned
        所以这个方法对节点的操控性非常强.
        */
        builder.on(Heading.class, new MarkwonVisitor.NodeVisitor<Heading>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Heading heading) {
                /*
                visitor.blockStart(heading);
                final int start = visitor.builder().length();
                visitor.visitChildren(heading);
                CoreProps.HEADING_LEVEL.set(visitor.renderProps(), heading.getLevel());

                这里能获取到factory工厂是由configureSpansFactory方法来保证的.
                如果一个节点有多个通过configureSpansFactory注册了多个工厂,则这里返回的工厂就是一个CompositeSpanFactory称之为符合工厂.



                final SpanFactory factory = visitor.configuration().spansFactory().get(heading.getClass());
                if (factory != null) {

                    这里factory.getSpans()方法返回的实际上是HeadingSpan实例对象
                    setSpans、setSpansForNode、setSpansForNodeOptional这三个方法本质上并没有什么区别


                    visitor.setSpans(start, factory.getSpans(visitor.configuration(), visitor.renderProps()));
                }
                visitor.blockEnd(heading);

                 */

                visitor.blockStart(heading);

                final int length = visitor.length();
                visitor.visitChildren(heading);

                CoreProps.HEADING_LEVEL.set(visitor.renderProps(), heading.getLevel());

                visitor.setSpansForNodeOptional(heading, length);

                visitor.forceNewLine();
            }
        });


    }

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        // markDown的预解析
        return super.processMarkdown(markdown);
    }


    @Override
    public void beforeRender(@NonNull Node node) {
        /*
        当一个节点Node渲染之前会先调用该方法,你可以在该方法内检查或修改节点
        第一步:先解析节点,节点会被解析为Node节点
        第二步:根据解析出的Node节点,将其渲染为Spanned实例.
        而Spanned实例是通过SpannableBuilder、SpanFactory共同进行构建的.
        第三部:将Spanned实例应用到TextView上,Spanned是Android原生对象,也就是说最终渲染的对象是Spanned
        Node仅仅是对原文进行解析后解析出的对象.
        */
        super.beforeRender(node);
    }

    @Override
    public void afterRender(@NonNull Node node, @NonNull MarkwonVisitor visitor) {
        // 当一个节点渲染完成之后会调用该方法,你可以在该方法内检查节点,该方法调用在Visitor之后
        super.afterRender(node, visitor);
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        // 在markdown应用到一个textView之前调用该方法
        super.beforeSetText(textView, markdown);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        // 在markdown应用到一个textView之后调用该方法
        super.afterSetText(textView);
    }

}
