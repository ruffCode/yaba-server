CREATE OR REPLACE FUNCTION increment_times_unlinked()
    RETURNS TRIGGER AS $$
BEGIN
    if new.linked=false then
    NEW.times_unlinked = OLD.times_unlinked + 1;
    end if;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
