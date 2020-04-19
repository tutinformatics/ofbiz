package org.apache.ofbiz.jersey.contact;


public class ContactDTO {
        String partyId;
        String firstName;
        String lastName;



        public String getFirstName() {
                return firstName;
        }

        public void setFirstName(String firstName) {
                this.firstName = firstName;
        }
        public String getLastName() {
                return lastName;
        }

        public void setLastName(String lastName) {
                this.lastName = lastName;
        }

        public void setPartyId(String partyId) {
                this.partyId = partyId;
        }

        public String getPartyId() {
                return partyId;
        }
}
