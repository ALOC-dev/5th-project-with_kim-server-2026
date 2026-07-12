-- 기존 데이터에 location 채우기
UPDATE buildings SET location = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography
WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

UPDATE school_buildings SET location = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography
WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

UPDATE infrastructures SET location = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography
WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_buildings_location ON buildings USING GIST(location);
CREATE INDEX IF NOT EXISTS idx_school_buildings_location ON school_buildings USING GIST(location);
CREATE INDEX IF NOT EXISTS idx_infrastructures_location ON infrastructures USING GIST(location);

-- 트리거 함수
CREATE OR REPLACE FUNCTION sync_location()
RETURNS TRIGGER AS $$
BEGIN
    NEW.location = ST_SetSRID(ST_MakePoint(NEW.longitude, NEW.latitude), 4326)::geography;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 적용
CREATE TRIGGER trg_buildings_location
    BEFORE INSERT OR UPDATE ON buildings
                         FOR EACH ROW EXECUTE FUNCTION sync_location();

CREATE TRIGGER trg_school_buildings_location
    BEFORE INSERT OR UPDATE ON school_buildings
                         FOR EACH ROW EXECUTE FUNCTION sync_location();

CREATE TRIGGER trg_infrastructures_location
    BEFORE INSERT OR UPDATE ON infrastructures
                         FOR EACH ROW EXECUTE FUNCTION sync_location();