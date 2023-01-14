package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class PlayerFilterHelper {
    public static Specification<Player> playerFilter(String name,
                                                     String title,
                                                     Race race,
                                                     Profession profession,
                                                     Long after,
                                                     Long before,
                                                     Boolean banned,
                                                     Integer minExperience,
                                                     Integer maxExperience,
                                                     Integer minLevel,
                                                     Integer maxLevel
    ) {
       return new Specification<Player>() {
           @Override
           public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
               List<Predicate> predicates = new ArrayList<>();
               if (Objects.nonNull(name)){
                   predicates.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
               }
               if (Objects.nonNull(title)){
                   predicates.add(criteriaBuilder.like(root.get("title"), "%"+title+"%"));
               }
               if (Objects.nonNull(race)){
                   predicates.add(criteriaBuilder.equal(root.get("race"), race));
               }
               if (Objects.nonNull(profession)){
                   predicates.add(criteriaBuilder.equal(root.get("profession"), profession));
               }
               if (Objects.nonNull(after)){
                   predicates.add(criteriaBuilder.greaterThan(root.get("birthday"), new Date(after)));
               }
               if (Objects.nonNull(before)){
                   predicates.add(criteriaBuilder.lessThan(root.get("birthday"), new Date(before)));
               }
               if (Objects.nonNull(banned)) {
                   predicates.add(criteriaBuilder.equal(root.get("banned"), banned));
               }
               if (Objects.nonNull(minExperience)) {
                   predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExperience));
               }
               if (Objects.nonNull(maxExperience)){
                   predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExperience));
               }
               if (Objects.nonNull(minLevel)){
                   predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("level"), minLevel));
               }
               if (Objects.nonNull(maxLevel)){
                   predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("level"), maxLevel));
               }
               return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
           }

           @Override
           public Specification<Player> and(Specification<Player> other) {
               return Specification.super.and(other);
           }

           @Override
           public Specification<Player> or(Specification<Player> other) {
               return Specification.super.or(other);
           }

       };
    }
}
