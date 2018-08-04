module Api::Entities
  class DropboxFolderEntity < Grape::Entity
    expose :path_lower, as: :id
    expose :name, as: :name
    expose :has_thumbnail
    expose :thumbnail_link
    expose :created_time do |instance, options|
      instance.instance_of?(Dropbox::FileMetadata) ? instance.client_modified : nil
    end
    expose :mime_type do |instance, options|
      instance.instance_of?(Dropbox::FileMetadata) ? "file" : "folder"
    end
    expose :size do |instance, options|
      instance.instance_of?(Dropbox::FileMetadata) ? instance.size : nil
    end

    private
    def has_thumbnail
        false
    end
    def thumbnail_link
        nil
    end
  end
end
