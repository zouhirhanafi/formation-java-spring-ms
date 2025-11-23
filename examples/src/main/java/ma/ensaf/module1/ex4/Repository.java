package ma.ensaf.module1.ex4;

import java.util.*;

public class Repository<T extends IEntity> {
        private Map<Long, T> storage = new HashMap<>();
        private Long nextId = 1L;

        public T save(T entity) {
            if (entity == null) {
                throw new IllegalArgumentException("The entity must not be null");
            }
            Long id = nextId++;
            entity.setId(id);
            storage.put(id, entity);
            return entity;
        }

        public Optional<T> findById(Long id) {
            return Optional.ofNullable(storage.get(id));
        }

        public boolean delete(Long id) {
            if (storage.containsKey(id)) {
                storage.remove(id);
                return true;
            }
            return false;
        }
        public List<T> findAll(){
            return new ArrayList<>(storage.values());
        }
        public long count() {
            return storage.size();
        }
}
 