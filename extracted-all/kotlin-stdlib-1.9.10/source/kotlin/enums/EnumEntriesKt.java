package kotlin.enums

@PublishedApi
@SinceKotlin(version = "1.8")
internal fun <E : Enum<E>> enumEntries(entriesProvider: () -> Array<E>): EnumEntries<E> {
   return new EnumEntriesList(entriesProvider.invoke() as java.lang.Enum[]);
}

@PublishedApi
@SinceKotlin(version = "1.8")
internal fun <E : Enum<E>> enumEntries(entries: Array<E>): EnumEntries<E> {
   return new EnumEntriesList(entries);
}

