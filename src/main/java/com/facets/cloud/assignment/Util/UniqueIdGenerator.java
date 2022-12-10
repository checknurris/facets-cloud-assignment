package com.facets.cloud.assignment.Util;

import com.facets.cloud.assignment.domains.ConnectionGroup;
import com.facets.cloud.assignment.domains.VirtualNode;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

public class UniqueIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        if(object instanceof VirtualNode){
            return "vnode_" + generateRandomString(10);
        } else if (object instanceof ConnectionGroup){
            return "cg_" + generateRandomString(11);
        }
        return UUID.randomUUID().toString();
    }

    static String generateRandomString(int targetString){
        Random rand = new Random();

        String generatedString = rand.ints(48, 123)
                .filter(num -> (num<58 || num>64) && (num<91 || num>96))
                .limit(targetString)
                .mapToObj(c -> (char)c).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
                .toString();

        return generatedString;
    }
}
