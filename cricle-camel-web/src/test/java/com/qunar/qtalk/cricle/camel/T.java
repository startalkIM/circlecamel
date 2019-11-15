package com.qunar.qtalk.cricle.camel;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class T {

    public static void main(String[] args) {
        Po one = Po.builder().id(1).data("one").list(Lists.newArrayList("one-1", "one-2", "one-3")).build();
        Po two = Po.builder().id(2).data("two").list(Lists.newArrayList("two-1", "two-2", "two-3")).build();

        List<Po> pos = Lists.newArrayList(one, two);
        pos.forEach(System.out::println);
        pos.sort((o1,o2)->-Integer.compare(o1.getId(),o2.getId()));
        pos.forEach(System.out::println);



        Po po = pos.stream().sorted(Comparator.comparingInt(p -> p.getList().size()))
                .collect(Collectors.maxBy(Comparator.comparingInt(Po::getId))).get();
        System.out.println("po:" + po);

        System.out.println(pos.stream().collect(Collectors.summingInt(Po::getId)));

        System.out.println(pos.stream().mapToInt(Po::getId).sum());

        System.out.println(pos.stream()
                .map(Po::getId).reduce(Integer::sum).get());

        pos.stream()
                .map(Po::getId).reduce(Integer::sum).ifPresent(System.out::println);

        System.out.println(new Double(1.0 / 0.0));
        System.out.println(pos.stream().collect(Collectors.averagingInt(Po::hashCode)).isInfinite());

        System.out.println(pos.stream().collect(Collectors.reducing(50, Po::getId, Integer::sum)));

        /*分组取个数*/
        Map<Integer, Long> map1 = pos.stream().collect(Collectors.groupingBy(Po::getId, Collectors.counting()));

        /**
         * 分组取最大对象
         */
        Map<Integer, Optional<Po>> collect = pos.stream().
                collect(Collectors.groupingBy(Po::getId, Collectors.maxBy(Comparator.comparingInt(Po::getId))));
        Map<Integer, Po> collect1 = pos.stream().collect(Collectors.groupingBy(Po::getId,
                Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingInt(Po::getId)), Optional::get)));
        Map<Integer, Integer> collect2 = pos.stream().collect(Collectors.groupingBy(Po::getId,
                Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingInt(Po::getId)), o -> o.get().getId())));


        System.out.println(ConfigurableWebApplicationContext.class.isAssignableFrom(WebApplicationContext.class));
        System.out.println(ConfigurableWebApplicationContext.class.isAssignableFrom(XmlWebApplicationContext.class));

        System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("java.class.path"));
        System.out.println(System.getProperty("user.dir"));
        System.out.println(System.getProperty("user.name"));
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("file.separator"));

        System.out.println(StringUtils.length("\n" +
                "\n"));

        System.out.println(StringUtils.length("恭喜！"));
        System.out.printf("f %s","hihao");
        //language=JSON
        String aaStr = "{\n" +
                "  \"username\": \"whling\",\n" +
                "  \"password\": \"123\"\n" +
                "}";
        if (aaStr != null) {
            System.out.printf(aaStr);
        }

    }

    @Data
    @Builder
    static class Po {
        private Integer id;

        private String data;

        private List<String> list;
    }
}
