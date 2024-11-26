package com.nembx.mypan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nembx.mypan.mapper.ChunkMapper;
import com.nembx.mypan.model.entity.Chunk;
import com.nembx.mypan.service.ChunkService;
import org.springframework.stereotype.Service;

/**
 * @author Lian
 */

@Service
public class ChunkServiceImpl extends ServiceImpl<ChunkMapper, Chunk> implements ChunkService {
}
