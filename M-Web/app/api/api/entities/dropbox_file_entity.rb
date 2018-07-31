module Api::Entities
  class DropboxFileEntity < Grape::Entity
    expose :id, as: :id
    expose :name, as: :name
    expose :has_thumbnail, as: :has_thumbnail
    expose :thumbnail_link, as: :thumbnail_link
    expose :created_time, as: :created_time
    expose :mime_type, as: :mime_type
    expose :size, as: :size
  end
end
