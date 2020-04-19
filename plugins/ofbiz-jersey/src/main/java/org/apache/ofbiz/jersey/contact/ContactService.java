package org.apache.ofbiz.jersey.contact;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;
import java.util.stream.Collectors;



public class ContactService {

    Delegator delegator;
    DispatchContext dispatchContext;

    public ContactService(DispatchContext dpc) {
        dispatchContext = dpc;
        delegator = dpc.getDelegator();
    }


    public List<ContactDTO> getContacts() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("Person").queryList();
        return genericValues.stream().map(x -> getContactDTO((String) x.get("firstName"))).collect(Collectors.toList());
    }

    public ContactDTO getContactDTO(String name) {
        ContactDTO contactDTO = new ContactDTO();

        GenericValue contact = null;
        try {
            contact = EntityQuery
                    .use(delegator)
                    .from("Person")
                        .where("firstName", name)
                    .queryOne();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        contactDTO.setPartyId((String) contact.get("partyId"));
        contactDTO.setFirstName((String) contact.get("firstName"));
        contactDTO.setLastName((String) contact.get("lastName"));


        return contactDTO;
    }


}
