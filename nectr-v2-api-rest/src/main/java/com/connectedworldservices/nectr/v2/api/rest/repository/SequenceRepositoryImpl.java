package com.connectedworldservices.nectr.v2.api.rest.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import com.connectedworldservices.nectr.v2.api.rest.model.Sequence;

public class SequenceRepositoryImpl implements SequenceRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public SequenceRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public long incrementSequenceId(String id) {

        Update update = new Update();
        update.inc("counter", 1L);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        options.upsert(true);

        Sequence sequence = mongoTemplate.findAndModify(Queries.searchById(id), update, options, Sequence.class);

        if (sequence == null) {
            throw new IllegalStateException("Unable to increment sequence for: " + id);
        }

        return sequence.getCounter();
    }

}
