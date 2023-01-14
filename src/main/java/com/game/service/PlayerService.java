package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PlayerService {
    List<Player> getAll(String name,
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
                        Integer pageSize);

    Integer getPlayerCount(String name,
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
    );

    Player createPlayer(Player newPlayer);

    Player getPlayerById(Long id);
    Player deletePlayerById(Long id);
    Player editPlayerById(Long id, Player player, Boolean isEmptyBody);

}
