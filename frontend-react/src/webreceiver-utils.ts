const groupToOrg = ( group: String ): String => {
    // in order to replace all instances of the underscore we needed to use a
    // global regex instead of a string. a string pattern only replaces the first
    // instance
    const re = /_/g;
    return group.toUpperCase().startsWith("DH")? group.slice(2).replace(re,'-') : group.replace(re,'-');
};

export { groupToOrg }