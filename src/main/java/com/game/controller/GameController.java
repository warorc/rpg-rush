package com.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.game.entity.Player;
import com.game.controller.PlayerOrder;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.service.BadRequestException;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController()
public class GameController {
    @Autowired
    PlayerService playerService;

    @GetMapping("/rest/players")
    public List<Player> getPlayers(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(required = false) Race race,
                                   @RequestParam(required = false) Profession profession,
                                   @RequestParam(required = false) Long after,
                                   @RequestParam(required = false) Long before,
                                   @RequestParam(required = false) Boolean banned,
                                   @RequestParam(required = false) Integer minExperience,
                                   @RequestParam(required = false) Integer maxExperience,
                                   @RequestParam(required = false) Integer minLevel,
                                   @RequestParam(required = false) Integer maxLevel,
                                   @RequestParam(required = false) PlayerOrder order,
                                   @RequestParam(required = false) Integer pageNumber,
                                   @RequestParam(required = false) Integer pageSize
    ) throws JsonProcessingException {
        List<Player> players =  playerService.getAll(name,
                title,
                race,
                profession,
                after,
                before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel,
                order,
                pageNumber,
                pageSize);
        return  players;
    }

    @GetMapping("/rest/players/count")
    public Integer getPlayersCount(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(required = false) Race race,
                                   @RequestParam(required = false) Profession profession,
                                   @RequestParam(required = false) Long after,
                                   @RequestParam(required = false) Long before,
                                   @RequestParam(required = false) Boolean banned,
                                   @RequestParam(required = false) Integer minExperience,
                                   @RequestParam(required = false) Integer maxExperience,
                                   @RequestParam(required = false) Integer minLevel,
                                   @RequestParam(required = false) Integer maxLevel) {
        return playerService.getPlayerCount(name,
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
        );
    }

    @PostMapping("/rest/players/")
    public Player createPlayer(@RequestBody Player newPlayer) {
        if (Objects.isNull(newPlayer.getName())
                || Objects.isNull(newPlayer.getTitle())
                || Objects.isNull(newPlayer.getRace())
                || Objects.isNull(newPlayer.getProfession())
                || Objects.isNull(newPlayer.getBirthday())
                || Objects.isNull(newPlayer.getExperience())) throw new BadRequestException("Empty fields");
        Calendar calendar = new GregorianCalendar();
        calendar.set(2000, Calendar.JANUARY, 1);
        long twoThous = calendar.getTimeInMillis();
        calendar.set(3000, Calendar.DECEMBER, 31);
        long threeThous = calendar.getTimeInMillis();
        if(newPlayer.getName().length() > 12
                || newPlayer.getTitle().length() > 30
                || newPlayer.getName().isEmpty()
                || newPlayer.getExperience() > 10000000
                || newPlayer.getExperience() < 0
                || newPlayer.getBirthday().getTime() < 0
                || twoThous >= newPlayer.getBirthday().getTime()
                || threeThous <= newPlayer.getBirthday().getTime()
        ) throw new BadRequestException("Something went wrong");
        if (Objects.isNull(newPlayer.getBanned())) newPlayer.setBanned(false);

        int level = ((int)Math.sqrt(2500 + 200*newPlayer.getExperience()) - 50)/100;
        newPlayer.setLevel(level);

        int untilNextLevel = (50 * (level + 1) * (level + 2)) - newPlayer.getExperience();
        newPlayer.setUntilNextLevel(untilNextLevel);

        return playerService.createPlayer(newPlayer);
    }

    @GetMapping("/rest/players/{id}")
    public Player getPlayerById(@PathVariable String id) {
        long idL;
        try {
            idL = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw  new BadRequestException("Not valid id");
        }
        if (idL <= 0) throw new BadRequestException("Not valid id");
        return playerService.getPlayerById(idL);
    }

    @PostMapping("/rest/players/{id}")
    public Player editPlayerById(@PathVariable String id, @RequestBody Player player) {
        boolean isEmptyBody = false;
        if (Objects.isNull(player.getName())
                && Objects.isNull(player.getTitle())
                && Objects.isNull(player.getRace())
                && Objects.isNull(player.getProfession())
                && Objects.isNull(player.getBirthday())
                && Objects.isNull(player.getExperience())) isEmptyBody = true;
        Calendar calendar = new GregorianCalendar();
        calendar.set(2000, Calendar.JANUARY, 1);
        long twoThous = calendar.getTimeInMillis();
        calendar.set(3000, Calendar.DECEMBER, 31);
        long threeThous = calendar.getTimeInMillis();
        if((Objects.nonNull(player.getName()) && player.getName().length() > 12)
                || (Objects.nonNull(player.getTitle()) && player.getTitle().length() > 30)
                || (Objects.nonNull(player.getExperience()) && player.getExperience() > 10000000)
                || (Objects.nonNull(player.getExperience()) && player.getExperience() < 0)
                || (Objects.nonNull(player.getBirthday()) && player.getBirthday().getTime() < 0)
                || (Objects.nonNull(player.getBirthday()) && twoThous >= player.getBirthday().getTime())
                || (Objects.nonNull(player.getBirthday()) && threeThous <= player.getBirthday().getTime())
        ) throw new BadRequestException("Something went wrong");
        long idL;
        try {
            idL = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw  new BadRequestException("Not valid id");
        }
        if (idL <= 0) throw new BadRequestException("Not valid id");

        Integer level = null;
        Integer untilNextLevel = null;
        if (Objects.nonNull(player.getExperience())) {
            level = ((int) Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
            player.setLevel(level);
            untilNextLevel = (50 * (level + 1) * (level + 2)) - player.getExperience();
            player.setUntilNextLevel(untilNextLevel);
        }
        return playerService.editPlayerById(idL, player, isEmptyBody);
    }

    @DeleteMapping("/rest/players/{id}")
    public void deletePlayerById(@PathVariable String id) {
        long idL;
        try {
            idL = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw  new BadRequestException("Not valid id");
        }
        if (idL <= 0) throw new BadRequestException("Not valid id");
        playerService.deletePlayerById(idL);
    }
}
