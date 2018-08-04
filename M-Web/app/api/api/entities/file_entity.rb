module Api::Entities
  class FileEntity < Grape::Entity
    expose :id
    expose :name
    expose :created_time
    expose :url
    expose :mime_type
    expose :size
  end
end
