package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerFilterHelper;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository playerRepo;

    @Override
    public List<Player> getAll(String name,
                               String title,
                               Race race,
                               Profession profession,
                               Long after,
                               Long before,
                               Boolean banned,
                               Integer minExperience,
                               Integer maxExperience,
                               Integer minLevel,
                               Integer maxLevel,
                               PlayerOrder order,
                               Integer pageNumber,
                               Integer pageSize) {
        if (order == null) order = PlayerOrder.ID;
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;
        Sort sort = Sort.by(Sort.Direction.ASC, order.getFieldName());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return playerRepo.findAll(PlayerFilterHelper.playerFilter(
                name,
                title,
                race,
                profession,
                after,
                before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel
        ), pageable).toList();
    }


    @Override
    public Integer getPlayerCount(String name,
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
        return Long.valueOf(playerRepo.count(PlayerFilterHelper.playerFilter(
                name,
                title,
                race,
                profession,
                after,
                before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel
        ))).intValue();
    }

    public Player createPlayer(Player newPlayer) {
        return playerRepo.save(newPlayer);
    }
    @Override
    public Player getPlayerById(Long id) {
        return playerRepo.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Player deletePlayerById(Long id) {
        return playerRepo.findById(id).map(p -> {
            playerRepo.deleteById(id);
            return p;
        }).orElseThrow(NotFoundException::new);
    }

    @Override
    public Player editPlayerById(Long id, Player player, Boolean isEmptyBody) {
        if (isEmptyBody) return playerRepo.findById(id).orElseThrow(NotFoundException::new);
        return playerRepo.findById(id).map(p -> {
            if (Objects.nonNull(player.getName()))
                p.setName(player.getName());
            if (Objects.nonNull(player.getTitle()))
                p.setTitle(player.getTitle());
            if (Objects.nonNull(player.getRace()))
                p.setRace(player.getRace());
            if (Objects.nonNull(player.getProfession()))
                p.setProfession(player.getProfession());
            if (Objects.nonNull(player.getBirthday()))
                p.setBirthday(player.getBirthday());
            if (Objects.nonNull(player.getBanned()))
                p.setBanned(player.getBanned());
            if (Objects.nonNull(player.getExperience())) {
                p.setExperience(player.getExperience());
                p.setLevel(player.getLevel());
                p.setUntilNextLevel(player.getUntilNextLevel());
            }
            return playerRepo.save(p);
        }).orElseThrow(NotFoundException::new);
    }

}
