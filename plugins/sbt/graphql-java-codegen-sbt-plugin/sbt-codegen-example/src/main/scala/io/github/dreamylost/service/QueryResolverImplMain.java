package io.github.dreamylost.service;

import io.github.dreamylost.model.DroidEntity;
import io.github.dreamylost.model.EpisodeEntity;
import io.github.dreamylost.model.HumanEntity;

import java.util.List;

/**
 * @author liguobin@growingio.com
 * @version 1.0, 2020/7/16
 * @see [[server code  https://github.com/jxnu-liguobin/springboot-examples/tree/master/graphql-complete]]
 */
public class QueryResolverImplMain {

    public static void main(String[] args) throws Exception {
        System.out.println("=======get droid id 2001=========");
        QueryResolverImpl droidResolver = new QueryResolverImpl();
        DroidEntity d = droidResolver.droid("2001");
        System.out.println(d);

        System.out.println("=======get humans all=======");
        List<HumanEntity> hums = droidResolver.humans();
        for (HumanEntity h : hums) {
            System.out.println(h);
        }

        System.out.println("=======get human id 1002=======");
        HumanEntity hum = droidResolver.human("1002");
        System.out.println(hum);


        System.out.println("=======get hero Episode.EMPIRE=======");
        io.github.dreamylost.model.CharacterEntity character = droidResolver.hero(EpisodeEntity.EMPIRE);
        System.out.println(character);


    }

}
