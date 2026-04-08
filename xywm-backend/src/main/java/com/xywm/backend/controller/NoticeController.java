package com.xywm.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xywm.backend.common.Result;
import com.xywm.backend.entity.Notice;
import com.xywm.backend.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeMapper noticeMapper;

    // 管理员：查所有公告
    @GetMapping("/list")
    public Result<List<Notice>> list() {
        List<Notice> list = noticeMapper.selectList(
                new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime)
        );
        return Result.success(list);
    }

    // 用户端：只查显示中的公告
    @GetMapping("/active")
    public Result<List<Notice>> activeList() {
        List<Notice> list = noticeMapper.selectList(
                new LambdaQueryWrapper<Notice>()
                        .eq(Notice::getStatus, 1)
                        .orderByDesc(Notice::getCreateTime)
        );
        return Result.success(list);
    }

    // 管理员：发布公告
    @PostMapping
    public Result<Void> add(@RequestBody Notice notice) {
        notice.setStatus(notice.getStatus() == null ? 1 : notice.getStatus());
        noticeMapper.insert(notice);
        return Result.success();
    }

    // 管理员：编辑公告
    @PutMapping
    public Result<Void> update(@RequestBody Notice notice) {
        noticeMapper.updateById(notice);
        return Result.success();
    }

    // 管理员：删除公告
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeMapper.deleteById(id);
        return Result.success();
    }
}