package com.axisrooms.db.query;

import com.axisrooms.db.column.ColumnInterface;

public enum EntityDBFactory {

  //TODO
//    USER_FACTORY(AbstractUser.Column.USER_TYPE) {
//        @Override
//        public Object getNewInstance(BaapSearchQuery< ? > searchQuery, ResultSet rs) throws Exception {
//            UserType userType = UserType.getUserTypeById(rs.getInt(getDiscriminator().getDataSetName(
//                    searchQuery.getJoinIdentifier())));
//            return userType.getUser();
//        }
//
//        @Override
//        public Object getNewInstance(LinkedHashMap<String, String> jsonAttributeMap) throws Exception {
//            UserType type = UserType.getUserTypeById(Integer.parseInt(jsonAttributeMap.get("type")));
//            return type.getUser();
//        }
//    }
    ;

    private ColumnInterface m_discriminator;

    private EntityDBFactory(ColumnInterface discriminator) {
        m_discriminator = discriminator;
    }

    //TODO
//    public abstract Object getNewInstance(BaapSearchQuery< ? > searchQuery, ResultSet rs) throws Exception;

  //TODO
//    public abstract Object getNewInstance(LinkedHashMap<String, String> jsonAttributeMap) throws Exception;

    public ColumnInterface getDiscriminator() {
        return m_discriminator;
    }

    public void setDiscriminator(ColumnInterface discriminator) {
        m_discriminator = discriminator;
    }
}
