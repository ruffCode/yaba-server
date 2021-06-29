create or replace function increment_times_unlinked() returns trigger
    language plpgsql
as
$$
BEGIN
    if new.linked = false and old.linked = true then
        NEW.times_unlinked = OLD.times_unlinked + 1;
    end if;
    RETURN NEW;
END;
$$;
