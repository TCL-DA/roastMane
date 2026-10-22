package org.eclipse.jetty.http.pathmap;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.jetty.util.Index.Builder;
import org.eclipse.jetty.util.Index.Mutable;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Path Mappings")
public class PathMappings<E> extends AbstractMap<PathSpec, E> implements Iterable<MappedResource<E>>, Dumpable, Predicate<String> {
   private static final Logger LOG = LoggerFactory.getLogger(PathMappings.class);
   private static final int PREFIX_TAIL_LEN = 3;
   private final Set<MappedResource<E>> _mappings = new TreeSet<>(Entry.comparingByKey());
   private boolean _orderIsSignificant;
   private boolean _optimizedExact = true;
   private final Map<String, MappedResource<E>> _exactMap = new HashMap<>();
   private boolean _optimizedPrefix = true;
   private final Mutable<MappedResource<E>> _prefixMap = new Builder().caseSensitive(true).mutable().build();
   private boolean _optimizedSuffix = true;
   private final Mutable<MappedResource<E>> _suffixMap = new Builder().caseSensitive(true).mutable().build();
   private MappedResource<E> _servletRoot;
   private MappedResource<E> _servletDefault;

   @Override
   public Set<Entry<PathSpec, E>> entrySet() {
      return this._mappings;
   }

   public String dump() {
      return Dumpable.dump(this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(out, indent, this.toString(), new Object[]{this._mappings});
   }

   @ManagedAttribute(
      value = "mappings",
      readonly = true
   )
   public List<MappedResource<E>> getMappings() {
      return new ArrayList<>(this._mappings);
   }

   @Override
   public int size() {
      return this._mappings.size();
   }

   public void reset() {
      this._mappings.clear();
      this._prefixMap.clear();
      this._suffixMap.clear();
      this._optimizedExact = true;
      this._optimizedPrefix = true;
      this._optimizedSuffix = true;
      this._orderIsSignificant = false;
      this._servletRoot = null;
      this._servletDefault = null;
   }

   public Stream<MappedResource<E>> streamResources() {
      return this._mappings.stream();
   }

   public boolean removeIf(Predicate<MappedResource<E>> predicate) {
      return this._mappings.removeIf(predicate);
   }

   public List<MatchedResource<E>> getMatchedList(String path) {
      List<MatchedResource<E>> ret = new ArrayList<>();

      for (MappedResource<E> mr : this._mappings) {
         MatchedPath matchedPath = mr.getPathSpec().matched(path);
         if (matchedPath != null) {
            ret.add(new MatchedResource<>(mr.getResource(), mr.getPathSpec(), matchedPath));
         }
      }

      return ret;
   }

   public List<MappedResource<E>> getMatches(String path) {
      if (this._mappings.isEmpty()) {
         return Collections.emptyList();
      } else {
         boolean isRootPath = "/".equals(path);
         List<MappedResource<E>> matches = null;

         for (MappedResource<E> mr : this._mappings) {
            switch (mr.getPathSpec().getGroup()) {
               case ROOT:
                  if (isRootPath) {
                     if (matches == null) {
                        matches = new ArrayList<>();
                     }

                     matches.add(mr);
                  }
                  break;
               case DEFAULT:
                  if (isRootPath || mr.getPathSpec().matched(path) != null) {
                     if (matches == null) {
                        matches = new ArrayList<>();
                     }

                     matches.add(mr);
                  }
                  break;
               default:
                  if (mr.getPathSpec().matched(path) != null) {
                     if (matches == null) {
                        matches = new ArrayList<>();
                     }

                     matches.add(mr);
                  }
            }
         }

         return matches == null ? Collections.emptyList() : matches;
      }
   }

   public boolean test(String path) {
      if (this._mappings.isEmpty()) {
         return false;
      } else if (this._servletDefault != null) {
         return true;
      } else if (this._servletRoot != null && "/".equals(path)) {
         return true;
      } else {
         MappedResource<E> exact = this._exactMap.get(path);
         if (exact != null) {
            return true;
         } else {
            MappedResource<E> prefix = (MappedResource<E>)this._prefixMap.getBest(path);

            while (prefix != null) {
               PathSpec pathSpec = prefix.getPathSpec();
               if (pathSpec.matches(path)) {
                  return true;
               }

               int specLength = pathSpec.getSpecLength();
               prefix = specLength > 3 ? (MappedResource)this._prefixMap.getBest(path, 0, specLength - 3) : null;
            }

            if (!this._suffixMap.isEmpty()) {
               int i = Math.max(0, path.lastIndexOf("/"));

               while ((i = path.indexOf(46, i + 1)) > 0) {
                  MappedResource<E> suffix = (MappedResource<E>)this._suffixMap.get(path, i + 1, path.length() - i - 1);
                  if (suffix != null) {
                     MatchedPath matchedPath = suffix.getPathSpec().matched(path);
                     if (matchedPath != null) {
                        return true;
                     }
                  }
               }
            }

            if (this._orderIsSignificant) {
               for (MappedResource<E> mr : this._mappings) {
                  if (!(mr.getPathSpec() instanceof ServletPathSpec) && mr.getPathSpec().matches(path)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public MatchedResource<E> getMatched(String path) {
      if (this._mappings.isEmpty()) {
         return null;
      } else if (this._orderIsSignificant) {
         return this.getMatchedIteratively(path);
      } else if (this._servletRoot != null && "/".equals(path)) {
         return this._servletRoot.getPreMatched();
      } else {
         MappedResource<E> exact = this._exactMap.get(path);
         if (exact != null) {
            return exact.getPreMatched();
         } else {
            MappedResource<E> prefix = (MappedResource<E>)this._prefixMap.getBest(path);

            while (prefix != null) {
               PathSpec pathSpec = prefix.getPathSpec();
               MatchedPath matchedPath = pathSpec.matched(path);
               if (matchedPath != null) {
                  return new MatchedResource<>(prefix.getResource(), pathSpec, matchedPath);
               }

               int specLength = pathSpec.getSpecLength();
               prefix = specLength > 3 ? (MappedResource)this._prefixMap.getBest(path, 0, specLength - 3) : null;
            }

            if (!this._suffixMap.isEmpty()) {
               int i = Math.max(0, path.lastIndexOf("/"));

               while ((i = path.indexOf(46, i + 1)) > 0) {
                  MappedResource<E> suffix = (MappedResource<E>)this._suffixMap.get(path, i + 1, path.length() - i - 1);
                  if (suffix != null) {
                     MatchedPath matchedPath = suffix.getPathSpec().matched(path);
                     if (matchedPath != null) {
                        return new MatchedResource<>(suffix.getResource(), suffix.getPathSpec(), matchedPath);
                     }
                  }
               }
            }

            return this._servletDefault != null
               ? new MatchedResource<>(this._servletDefault.getResource(), this._servletDefault.getPathSpec(), this._servletDefault.getPathSpec().matched(path))
               : null;
         }
      }
   }

   private MatchedResource<E> getMatchedIteratively(String path) {
      PathSpecGroup lastGroup = null;
      boolean skipRestOfGroup = false;

      for (MappedResource<E> mr : this._mappings) {
         PathSpecGroup group = mr.getPathSpec().getGroup();
         if (group != lastGroup || !skipRestOfGroup) {
            if (group != lastGroup) {
               skipRestOfGroup = false;
               switch (group) {
                  case EXACT:
                     if (this._optimizedExact) {
                        MappedResource<E> exact = this._exactMap.get(path);
                        if (exact != null) {
                           return exact.getPreMatched();
                        }

                        skipRestOfGroup = true;
                     }
                     break;
                  case PREFIX_GLOB:
                     if (!this._optimizedPrefix) {
                        break;
                     }

                     MappedResource<E> prefix = (MappedResource<E>)this._prefixMap.getBest(path);

                     while (prefix != null) {
                        PathSpec pathSpec = prefix.getPathSpec();
                        MatchedPath matchedPath = pathSpec.matched(path);
                        if (matchedPath != null) {
                           return new MatchedResource<>(prefix.getResource(), pathSpec, matchedPath);
                        }

                        int specLength = pathSpec.getSpecLength();
                        prefix = specLength > 3 ? (MappedResource)this._prefixMap.getBest(path, 0, specLength - 3) : null;
                     }

                     skipRestOfGroup = true;
                     break;
                  case SUFFIX_GLOB:
                     if (this._optimizedSuffix) {
                        int i = 0;

                        while ((i = path.indexOf(46, i + 1)) > 0) {
                           MappedResource<E> suffix = (MappedResource<E>)this._suffixMap.get(path, i + 1, path.length() - i - 1);
                           if (suffix != null) {
                              MatchedPath matchedPath = suffix.getPathSpec().matched(path);
                              if (matchedPath != null) {
                                 return new MatchedResource<>(suffix.getResource(), suffix.getPathSpec(), matchedPath);
                              }
                           }
                        }

                        skipRestOfGroup = true;
                     }
               }
            }

            MatchedPath matchedPath = mr.getPathSpec().matched(path);
            if (matchedPath != null) {
               return new MatchedResource<>(mr.getResource(), mr.getPathSpec(), matchedPath);
            }

            lastGroup = group;
         }
      }

      return null;
   }

   @Override
   public Iterator<MappedResource<E>> iterator() {
      return this._mappings.iterator();
   }

   @Override
   public E get(Object key) {
      return key instanceof PathSpec pathSpec ? this.get(pathSpec) : null;
   }

   public E get(PathSpec pathSpec) {
      if (pathSpec == null) {
         return null;
      } else {
         for (MappedResource<E> mr : this._mappings) {
            if (pathSpec.equals(mr.getKey())) {
               return mr.getValue();
            }
         }

         return null;
      }
   }

   public E put(String pathSpecString, E resource) {
      return this.put(PathSpec.from(pathSpecString), resource);
   }

   public E put(PathSpec pathSpec, E resource) {
      E old = this.remove(pathSpec);
      MappedResource<E> entry = new MappedResource<>(pathSpec, resource);
      this._mappings.add(entry);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Added {} replacing {} to {}", new Object[]{entry, old, this});
      }

      switch (pathSpec.getGroup()) {
         case ROOT:
            if (pathSpec instanceof ServletPathSpec) {
               if (this._servletRoot == null) {
                  this._servletRoot = entry;
               }
            } else {
               this._orderIsSignificant = true;
            }
            break;
         case DEFAULT:
            if (pathSpec instanceof ServletPathSpec) {
               if (this._servletDefault == null) {
                  this._servletDefault = entry;
               }
            } else {
               this._orderIsSignificant = true;
            }
            break;
         case EXACT:
            if (pathSpec instanceof ServletPathSpec) {
               String exact = pathSpec.getDeclaration();
               if (exact != null) {
                  this._exactMap.put(exact, entry);
               }
            } else {
               this._optimizedExact = false;
               this._orderIsSignificant = true;
            }
            break;
         case PREFIX_GLOB:
            if (pathSpec instanceof ServletPathSpec) {
               String prefix = pathSpec.getPrefix();
               if (prefix != null) {
                  this._prefixMap.put(prefix, entry);
               }
            } else {
               this._optimizedPrefix = false;
               this._orderIsSignificant = true;
            }
            break;
         case SUFFIX_GLOB:
            if (pathSpec instanceof ServletPathSpec) {
               String suffix = pathSpec.getSuffix();
               if (suffix != null) {
                  this._suffixMap.put(suffix, entry);
               }
            } else {
               this._optimizedSuffix = false;
               this._orderIsSignificant = true;
            }
      }

      return old;
   }

   @Override
   public E remove(Object key) {
      return key instanceof PathSpec pathSpec ? this.remove(pathSpec) : null;
   }

   public E remove(PathSpec pathSpec) {
      Iterator<MappedResource<E>> iter = this._mappings.iterator();
      E removed = null;

      while (iter.hasNext()) {
         MappedResource<E> entry = iter.next();
         if (entry.getPathSpec().equals(pathSpec)) {
            removed = entry.getResource();
            iter.remove();
            break;
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Removed {} at {} from {}", new Object[]{removed, pathSpec, this});
      }

      if (removed != null) {
         switch (pathSpec.getGroup()) {
            case ROOT:
               this._servletRoot = this._mappings
                  .stream()
                  .filter(mapping -> mapping.getPathSpec().getGroup() == PathSpecGroup.ROOT)
                  .filter(mapping -> mapping.getPathSpec() instanceof ServletPathSpec)
                  .findFirst()
                  .orElse(null);
               this._orderIsSignificant = this.nonServletPathSpec();
               break;
            case DEFAULT:
               this._servletDefault = this._mappings
                  .stream()
                  .filter(mapping -> mapping.getPathSpec().getGroup() == PathSpecGroup.DEFAULT)
                  .filter(mapping -> mapping.getPathSpec() instanceof ServletPathSpec)
                  .findFirst()
                  .orElse(null);
               this._orderIsSignificant = this.nonServletPathSpec();
               break;
            case EXACT:
               String exact = pathSpec.getDeclaration();
               if (exact != null) {
                  this._exactMap.remove(exact);
                  this._optimizedExact = this.canBeOptimized(PathSpecGroup.EXACT);
                  this._orderIsSignificant = this.nonServletPathSpec();
               }
               break;
            case PREFIX_GLOB:
               String prefix = pathSpec.getPrefix();
               if (prefix != null) {
                  this._prefixMap.remove(prefix);
                  this._optimizedPrefix = this.canBeOptimized(PathSpecGroup.PREFIX_GLOB);
                  this._orderIsSignificant = this.nonServletPathSpec();
               }
               break;
            case SUFFIX_GLOB:
               String suffix = pathSpec.getSuffix();
               if (suffix != null) {
                  this._suffixMap.remove(suffix);
                  this._optimizedSuffix = this.canBeOptimized(PathSpecGroup.SUFFIX_GLOB);
                  this._orderIsSignificant = this.nonServletPathSpec();
               }
         }
      }

      return removed;
   }

   private boolean canBeOptimized(PathSpecGroup suffixGlob) {
      return this._mappings
         .stream()
         .filter(mapping -> mapping.getPathSpec().getGroup() == suffixGlob)
         .allMatch(mapping -> mapping.getPathSpec() instanceof ServletPathSpec);
   }

   private boolean nonServletPathSpec() {
      return this._mappings.stream().allMatch(mapping -> mapping.getPathSpec() instanceof ServletPathSpec);
   }

   @Override
   public String toString() {
      return String.format("%s[size=%d]", this.getClass().getSimpleName(), this._mappings.size());
   }
}
